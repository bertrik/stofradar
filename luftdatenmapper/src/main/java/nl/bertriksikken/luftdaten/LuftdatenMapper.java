package nl.bertriksikken.luftdaten;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import nl.bertriksikken.luftdaten.api.LuftDatenDataApi;
import nl.bertriksikken.luftdaten.api.dto.DataPoint;
import nl.bertriksikken.luftdaten.api.dto.DataPoints;
import nl.bertriksikken.luftdaten.api.dto.DataValue;
import nl.bertriksikken.luftdaten.api.dto.Location;
import nl.bertriksikken.luftdaten.api.dto.Sensor;
import nl.bertriksikken.luftdaten.config.LuftdatenConfig;
import nl.bertriksikken.luftdaten.config.LuftdatenMapperConfig;
import nl.bertriksikken.luftdaten.config.RenderJob;
import nl.bertriksikken.luftdaten.render.ColorMapper;
import nl.bertriksikken.luftdaten.render.ColorPoint;
import nl.bertriksikken.luftdaten.render.IShader;
import nl.bertriksikken.luftdaten.render.Interpolator;
import nl.bertriksikken.luftdaten.render.InverseDistanceWeightShader;
import nl.bertriksikken.luftdaten.render.SensorValue;

/**
 * Process the luftdaten JSON and produces a CSV with coordinates and weighted
 * dust averages.
 *
 */
public final class LuftdatenMapper {

    private static final Logger LOG = LoggerFactory.getLogger(LuftdatenMapper.class);
    private static final File SENSOR_VALUE_CACHE_FILE = new File("sensorvaluecache.json");

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final LuftdatenMapperConfig config;
    private final LuftDatenDataApi luftDatenApi;
    private final ObjectMapper objectMapper = new ObjectMapper();
    // map from id to sensor value
    private final Map<Integer, SensorValue> sensorValueMap = new HashMap<>();

    // color range according https://www.luchtmeetnet.nl/informatie/luchtkwaliteit/luchtkwaliteitsindex-(lki)
    private static final ColorPoint[] RANGE_PM2_5 = new ColorPoint[] {
            // good
            new ColorPoint(0, new int[] { 0, 100, 255, 0x00 }), 
            new ColorPoint(10, new int[] { 0, 175, 255, 0x60 }),
            new ColorPoint(15, new int[] { 150, 200, 255, 0xC0 }),
            // not so good
            new ColorPoint(20, new int[] { 255, 255, 200, 0xC0 }), 
            new ColorPoint(30, new int[] { 255, 255, 150, 0xC0 }),
            new ColorPoint(40, new int[] { 255, 255, 0, 0xC0 }),
            // insufficient
            new ColorPoint(50, new int[] { 255, 200, 0, 0xC0 }), 
            new ColorPoint(70, new int[] { 255, 150, 0, 0xC0 }),
            // bad
            new ColorPoint(90, new int[] { 255, 75, 0, 0xC0 }), 
            new ColorPoint(100, new int[] { 255, 25, 0, 0xC0 }),
            // very bad
            new ColorPoint(140, new int[] { 164, 58, 217, 0xC0 })
    };

    LuftdatenMapper(LuftdatenMapperConfig config) {
        this.config = config;
        luftDatenApi = LuftDatenDataApi.create(config.getLuftdatenConfig());
        objectMapper.findAndRegisterModules();
    }

    private List<SensorValue> filterBySensorValue(List<SensorValue> values) {
        List<SensorValue> filtered = values.stream().filter(v -> v.value >= 0.0).collect(Collectors.toList());
        LOG.info("Filtered by sensor value: {} -> {}", values.size(), filtered.size());
        return filtered;
    }

    private List<SensorValue> filterBySensorId(List<SensorValue> values, List<Integer> blacklist) {
        List<SensorValue> filtered = values.stream().filter(v -> !blacklist.contains(v.id))
                .collect(Collectors.toList());
        LOG.info("Filtered by sensor id: {} -> {}", values.size(), filtered.size());
        return filtered;
    }

    private List<SensorValue> filterByPercentile(List<SensorValue> rawValues, double perc) {
        List<SensorValue> copy = new ArrayList<>(rawValues);
        Collections.sort(copy, (v1, v2) -> Double.compare(v1.value, v2.value));
        int newSize = (int) ((1 - perc) * rawValues.size());
        List<SensorValue> filtered = copy.subList(0, newSize);
        LOG.info("Filtered by percentile filter: {} -> {}", rawValues.size(), filtered.size());
        return filtered;
    }

    public static void main(String[] args) throws IOException {
        PropertyConfigurator.configure("log4j.properties");

        LuftdatenMapperConfig config = readConfig(new File("luftdatenmapper.yaml"));
        LuftdatenMapper luftdatenMapper = new LuftdatenMapper(config);
        luftdatenMapper.start();
    }

    private static LuftdatenMapperConfig readConfig(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        try (FileInputStream fis = new FileInputStream(file)) {
            return mapper.readValue(fis, LuftdatenMapperConfig.class);
        } catch (IOException e) {
            LOG.warn("Failed to load config {}, writing defaults", file.getAbsoluteFile());
            LuftdatenMapperConfig config = new LuftdatenMapperConfig();
            mapper.writeValue(file, config);
            return config;
        }
    }

    private void start() throws IOException {
        // restore cache
        restoreSensorValues();
        
        // schedule immediate job for instant feedback
        executor.submit(() -> runDownloadAndProcess(0));

        // schedule periodic job
        Instant now = Instant.now();
        long initialDelay = 300L - (now.getEpochSecond() % 300L);
        executor.scheduleAtFixedRate(() -> runDownloadAndProcess(2), initialDelay, 300L,
                TimeUnit.SECONDS);
    }
    
    private void persistSensorValues(List<SensorValue> values) {
        LOG.info("Persisting {} sensor values to cache", values.size());
        try (FileOutputStream fos = new FileOutputStream(SENSOR_VALUE_CACHE_FILE)) {
            objectMapper.writeValue(fos, values);
            LOG.info("Persisting done");
        } catch (Throwable e) {
            LOG.warn("Could not persist sensor values", e);
        }
    }
    
    private void restoreSensorValues() {
        LOG.info("Restoring sensor values from cache");
        try (FileInputStream fos = new FileInputStream(SENSOR_VALUE_CACHE_FILE)) {
            List<SensorValue> values = objectMapper.readValue(fos, new TypeReference<List<SensorValue>>(){});
            values.forEach(v -> sensorValueMap.put(v.id, v));
            LOG.info("Restored {} sensor values from cache", values.size());
        } catch (Throwable e) {
            LOG.warn("Could not restore sensor values", e);
        }
    }

    private void runDownloadAndProcess(int retries) {
        // run the main process in a try-catch to protect the thread it runs on from
        // exceptions
        try {
            Instant now = Instant.now();
            downloadAndProcess(now);
        } catch (Throwable e) {
            LOG.error("Caught top-level throwable", e);
            if (retries > 0) {
                LOG.error("Retrying in 30 seconds, retries left: {}", retries);
                executor.schedule(() -> runDownloadAndProcess(retries - 1), 30L, TimeUnit.SECONDS);
            }
        }
    }

    private void downloadAndProcess(Instant now) throws IOException {
        // get UTC time rounded to 5 minutes
        ZonedDateTime utcTime = ZonedDateTime.ofInstant(now, ZoneId.of("UTC"));
        int minute = 5 * (utcTime.get(ChronoField.MINUTE_OF_HOUR) / 5);
        utcTime = utcTime.withMinute(minute).truncatedTo(ChronoUnit.MINUTES);

        // create temporary name
        File tempDir = new File(config.getIntermediateDir());
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            LOG.warn("Failed to create directory {}", tempDir.getAbsolutePath());
        }
        String fileName = String.format(Locale.ROOT, "%04d%02d%02d_%02d%02d.json", utcTime.get(ChronoField.YEAR),
                utcTime.get(ChronoField.MONTH_OF_YEAR), utcTime.get(ChronoField.DAY_OF_MONTH),
                utcTime.get(ChronoField.HOUR_OF_DAY), utcTime.get(ChronoField.MINUTE_OF_HOUR));
        File jsonFile = new File(tempDir, fileName);

        // delete output files for this time-of-day
        String pngName = String.format(Locale.ROOT, "%02d%02d.png", utcTime.getHour(), utcTime.getMinute());
        for (RenderJob job : config.getRenderJobs()) {
            File jobDir = new File(tempDir, job.getName());
            File outputFile = new File(jobDir, pngName);
            if (outputFile.exists()) {
                LOG.info("Deleting file {}", outputFile.getAbsolutePath());
                if (!outputFile.delete()) {
                    LOG.warn("Deletion failed");
                }
            }
        }

        // download JSON
        DataPoints dataPoints = downloadFile(jsonFile);

        // convert DataPoints to internal format
        List<SensorValue> sensorValues = convertDataPoints(dataPoints, "P2", now);

        // update list of sensor values, expiring old data
        sensorValues.forEach(v -> sensorValueMap.put(v.id, v));
        Instant expiryTime = now.minus(config.getKeepingDuration());
        sensorValueMap.entrySet().removeIf(e -> e.getValue().time.isBefore(expiryTime));
        List<SensorValue> rawValues = new ArrayList<>(sensorValueMap.values());
        
        // store cached value
        persistSensorValues(rawValues);
        
        // remove top percentile of measurements
        List<SensorValue> filteredValues = filterByPercentile(rawValues, 0.01);

        // filter by value and id
        filteredValues = filterBySensorValue(filteredValues);
        LuftdatenConfig luftdatenConfig = config.getLuftdatenConfig();
        filteredValues = filterBySensorId(filteredValues, luftdatenConfig.getBlacklist());

        // render all jobs
        for (RenderJob job : config.getRenderJobs()) {
            File jobDir = new File(tempDir, job.getName());
            if (jobDir.mkdirs()) {
                LOG.info("Created directory {}", jobDir);
            }
            File outputFile = new File(config.getOutputPath(), job.getName() + ".png");
            render(job, jobDir, filteredValues, utcTime, outputFile);
            // copy file for animation
            File animationFile = new File(jobDir, pngName);
            Files.copy(outputFile.toPath(), animationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        // delete JSON
        if (!jsonFile.delete()) {
            LOG.warn("Failed to delete JSON file");
        }
    }

    private void render(RenderJob job, File jobDir, List<SensorValue> filteredValues, ZonedDateTime utcTime,
            File outputFile) {

        // apply bounding box
        List<SensorValue> boundedValues = filterByBoundingBox(filteredValues, job);

        try {
            // create overlay
            ColorMapper colorMapper = new ColorMapper(RANGE_PM2_5);
            File overlayFile = new File(jobDir, "overlay.png");
            renderDust(boundedValues, overlayFile, colorMapper, job);

            // create composite from background image and overlay
            File baseMap = new File(job.getMapFile());
            File compositeFile = new File(jobDir, "composite.png");
            composite(config.getCompositeCmd(), overlayFile, baseMap, compositeFile);

            // add timestamp to composite
            LocalDateTime localDateTime = LocalDateTime.ofInstant(utcTime.toInstant(), ZoneId.systemDefault());
            String timestampText = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            timestamp(config.getConvertCmd(), timestampText, compositeFile, outputFile);
        } catch (IOException e) {
            LOG.trace("Caught IOException", e);
            LOG.warn("Caught IOException: {}", e.getMessage());
        }
    }

    /**
     * Filters sensor values by position according to a bounding box. The size of
     * the bounding box is 3x3 times the size of the render job.
     * 
     * @param values the sensor values
     * @param job    the render job
     * @return values filtered by position
     */
    private List<SensorValue> filterByBoundingBox(List<SensorValue> values, RenderJob job) {
        double minX = 1.5 * job.getWest() - 0.5 * job.getEast();
        double maxX = 1.5 * job.getEast() - 0.5 * job.getWest();
        double minY = 1.5 * job.getSouth() - 0.5 * job.getNorth();
        double maxY = 1.5 * job.getNorth() - 0.5 * job.getSouth();
        List<SensorValue> filtered = values.stream().filter(v -> (v.x > minX)).filter(v -> (v.x < maxX))
                .filter(v -> (v.y > minY)).filter(v -> (v.y < maxY)).collect(Collectors.toList());
        LOG.info("Filtered by bounding box: {} -> {}", values.size(), filtered.size());
        return filtered;
    }

    /**
     * Downloads a new JSON dust file.
     * 
     * @param file the file to download to
     * @return the parsed contents
     * @throws IOException
     */
    private DataPoints downloadFile(File file) throws IOException {
        LOG.info("Downloading new dataset to {}", file);
        luftDatenApi.downloadDust(file);

        LOG.info("Decoding dataset ...");
        return objectMapper.readValue(file, DataPoints.class);
    }

    /**
     * Converts from the luftdaten datapoints format to internal format.
     * 
     * @param dataPoints the data points
     * @param item       which item to select (P1 or P2)
     * @return list of sensor values
     */
    private List<SensorValue> convertDataPoints(DataPoints dataPoints, String item, Instant instant) {
        List<SensorValue> values = new ArrayList<>();
        int numIndoor = 0;
        for (DataPoint dp : dataPoints) {
            Sensor sensor = dp.getSensor();
            Location l = dp.getLocation();
            if (l.getIndoor() != 0) {
                numIndoor++;
                continue;
            }
            DataValue dataValue = dp.getSensorDataValues().getDataValue(item);
            if (dataValue != null) {
                int id = sensor.getId();
                double x = l.getLongitude();
                double y = l.getLatitude();
                double v = dataValue.getValue();
                values.add(new SensorValue(id, x, y, v, instant));
            }
        }
        LOG.info("Ignored {} indoor sensors", numIndoor);
        return values;
    }

    /**
     * Renders a JSON file to a PNG.
     * 
     * @param sensorValues the data points
     * @param pngFile      the PNG file
     * @param colorMapper  the color mapper
     * @throws IOException
     */
    private void renderDust(List<SensorValue> sensorValues, File pngFile, ColorMapper colorMapper, RenderJob job)
            throws IOException {
        LOG.info("Rendering {} data points to {}", sensorValues.size(), pngFile);

        // parse background file
        File mapFile = new File(job.getMapFile());
        BufferedImage mapImage = ImageIO.read(mapFile);
        int width = mapImage.getWidth();
        int height = mapImage.getHeight();

        // prepare output file
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = image.getRaster();

        // interpolate over grid
        IShader shader = new InverseDistanceWeightShader(job, colorMapper);
        Interpolator interpolator = new Interpolator(job, shader, width, height);
        interpolator.interpolate(sensorValues, raster);

        // save it
        LOG.info("Writing to {}", pngFile);
        ImageIO.write(image, "png", pngFile);
    }

    /**
     * Composites a combined image of a fine dust overlay over a base map.
     * 
     * @param command path to the imagemagick 'composite command'
     * @param overlay the dust overlay image
     * @param baseMap the base map image
     * @param outFile the combined image
     * @throws IOException
     */
    private void composite(String command, File overlay, File baseMap, File outFile) throws IOException {
        LOG.info("Compositing {} over {} to {}", overlay, baseMap, outFile);

        // parse background file
        BufferedImage mapImage = ImageIO.read(baseMap);
        String composeArg = String.format(Locale.ROOT, "%dx%d", mapImage.getWidth(), mapImage.getHeight());

        List<String> arguments = new ArrayList<>();
        arguments.add(command);
        arguments.addAll(Arrays.asList("-compose", "over"));
        arguments.addAll(Arrays.asList("-geometry", composeArg));
        arguments.add(overlay.getAbsolutePath());
        arguments.add(baseMap.getAbsolutePath());
        arguments.add(outFile.getAbsolutePath());
        ProcessBuilder pb = new ProcessBuilder(arguments);
        try {
            Process process = pb.start();
            int exitValue = process.waitFor();
            if (exitValue != 0) {
                InputStream is = process.getErrorStream();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        LOG.info("line: {}", line);
                    }
                }
            }
            LOG.info("Process ended with {}", exitValue);
        } catch (Exception e) {
            LOG.trace("Caught IOException", e);
            LOG.warn("Caught IOException: {}", e.getMessage());
        }
    }

    private void timestamp(String command, String timestampText, File compositeFile, File outputFile) {
        LOG.info("Timestamping {} over {} to {}", timestampText, compositeFile, outputFile);

        List<String> arguments = new ArrayList<>();
        arguments.add(command);
        arguments.addAll(Arrays.asList("-gravity", "northwest"));
        arguments.addAll(Arrays.asList("-pointsize", "30"));
        arguments.addAll(Arrays.asList("-undercolor", "dimgrey"));
        arguments.addAll(Arrays.asList("-fill", "white"));
        arguments.addAll(Arrays.asList("-annotate", "0"));
        arguments.add(timestampText);
        arguments.add(compositeFile.getAbsolutePath());
        arguments.add(outputFile.getAbsolutePath());
        ProcessBuilder pb = new ProcessBuilder(arguments);
        try {
            Process process = pb.start();
            int exitValue = process.waitFor();
            if (exitValue != 0) {
                InputStream is = process.getErrorStream();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        LOG.info("line: {}", line);
                    }
                }
            }
            LOG.info("Process ended with {}", exitValue);
        } catch (Exception e) {
            LOG.trace("Caught IOException", e);
            LOG.warn("Caught IOException: {}", e.getMessage());
        }
    }

}
