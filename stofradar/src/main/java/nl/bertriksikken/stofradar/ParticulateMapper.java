package nl.bertriksikken.stofradar;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import nl.bertriksikken.stofradar.config.ParticulateMapperConfig;
import nl.bertriksikken.stofradar.config.RenderJob;
import nl.bertriksikken.stofradar.meetjestad.MeetjestadData;
import nl.bertriksikken.stofradar.meetjestad.MeetjestadDownloader;
import nl.bertriksikken.stofradar.render.ColorMapper;
import nl.bertriksikken.stofradar.render.ColorPoint;
import nl.bertriksikken.stofradar.render.EDataSource;
import nl.bertriksikken.stofradar.render.IShader;
import nl.bertriksikken.stofradar.render.Interpolator;
import nl.bertriksikken.stofradar.render.InverseDistanceWeightShader;
import nl.bertriksikken.stofradar.render.SensorValue;
import nl.bertriksikken.stofradar.restapi.AirRestApi;
import nl.bertriksikken.stofradar.restapi.AirRestServer;
import nl.bertriksikken.stofradar.samenmeten.csv.SamenmetenCsv;
import nl.bertriksikken.stofradar.samenmeten.csv.SamenmetenCsvDownloader;
import nl.bertriksikken.stofradar.samenmeten.csv.SamenmetenCsvLuchtEntry;
import nl.bertriksikken.stofradar.senscom.SensComDataApi;
import nl.bertriksikken.stofradar.senscom.dto.DataPoint;
import nl.bertriksikken.stofradar.senscom.dto.DataValue;
import nl.bertriksikken.stofradar.senscom.dto.Location;
import nl.bertriksikken.stofradar.senscom.dto.Sensor;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
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
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Process the sensor.community JSON and produces a CSV with coordinates and
 * weighted dust averages.
 */
public final class ParticulateMapper {

    private static final Logger LOG = LoggerFactory.getLogger(ParticulateMapper.class);
    private static final File SENSOR_VALUE_CACHE_FILE = new File("sensorvaluecache.json");

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final ParticulateMapperConfig config;
    private final SensComDataApi sensComDataApi;
    private final ObjectMapper objectMapper = new ObjectMapper();
    // map from id to sensor value
    private final Map<String, SensorValue> sensorValueMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> plausibilityMap = new ConcurrentHashMap<>();
    private final SamenmetenCsvDownloader samenmetenDownloader;
    private final MeetjestadDownloader meetjestadDownloader;
    private final AirRestServer pmRestApiHandler;

    // color range according
    // https://www.luchtmeetnet.nl/informatie/luchtkwaliteit/luchtkwaliteitsindex-(lki)
    private static final ColorPoint[] RANGE_PM2_5 = new ColorPoint[]{
            // good
            new ColorPoint(0, new int[]{0, 100, 255, 0x00}), new ColorPoint(10, new int[]{0, 175, 255, 0x60}),
            new ColorPoint(15, new int[]{150, 200, 255, 0xC0}),
            // not so good
            new ColorPoint(20, new int[]{255, 255, 200, 0xC0}),
            new ColorPoint(30, new int[]{255, 255, 150, 0xC0}), new ColorPoint(40, new int[]{255, 255, 0, 0xC0}),
            // insufficient
            new ColorPoint(50, new int[]{255, 200, 0, 0xC0}), new ColorPoint(70, new int[]{255, 150, 0, 0xC0}),
            // bad
            new ColorPoint(90, new int[]{255, 75, 0, 0xC0}), new ColorPoint(100, new int[]{255, 25, 0, 0xC0}),
            // very bad
            new ColorPoint(140, new int[]{164, 58, 217, 0xC0})};
    private final ColorMapper colorMapper = new ColorMapper(RANGE_PM2_5);

    ParticulateMapper(ParticulateMapperConfig config) {
        this.config = config;
        objectMapper.findAndRegisterModules();
        sensComDataApi = SensComDataApi.create(config.sensComConfig, objectMapper);
        samenmetenDownloader = SamenmetenCsvDownloader.create(config.samenmetenConfig);
        meetjestadDownloader = MeetjestadDownloader.create(config.meetjestadConfig, objectMapper);
        pmRestApiHandler = new AirRestServer(config.airRestApiConfig);
    }

    // entirely removes obviously invalid sensor values (e.g. negative PM)
    private List<SensorValue> filterBySensorValue(List<SensorValue> values) {
        List<SensorValue> filtered = values.stream().filter(v -> v.value >= 0.0).collect(Collectors.toList());
        LOG.info("Filtered by sensor value: {} -> {}", values.size(), filtered.size());
        return filtered;
    }

    private List<SensorValue> scoreByBlockList(List<SensorValue> values, int score) {
        List<SensorValue> blocked = values.stream().filter(v -> config.blockList.contains(v.id)).toList();
        blocked.forEach(v -> v.setPlausibility(score));
        LOG.info("Scored {} sensors from block list as improbable", blocked.size());
        return values;
    }

    private List<SensorValue> scoreByPercentile(List<SensorValue> values, double perc, int score) {
        List<SensorValue> copy = new ArrayList<>(values);
        copy.sort((v1, v2) -> Double.compare(v2.value, v1.value));
        for (int i = 0; i < (perc * values.size()); i++) {
            SensorValue value = copy.get(i);
            if (value.getPlausibility() >= 0) {
                value.setPlausibility(Math.min(value.getPlausibility(), score));
            } else {
                value.setPlausibility(score);
            }
        }
        return copy;
    }

    public static void main(String[] args) throws IOException {
        PropertyConfigurator.configure("log4j.properties");

        ParticulateMapperConfig config = readConfig(new File("stofradar.yaml"));
        ParticulateMapper particulateMapper = new ParticulateMapper(config);
        particulateMapper.start();
    }

    private static ParticulateMapperConfig readConfig(File file) throws IOException {
        ParticulateMapperConfig config = new ParticulateMapperConfig();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                config = mapper.readValue(fis, ParticulateMapperConfig.class);
            } catch (IOException e) {
                LOG.warn("Failed to load config {}, using defaults", file.getAbsoluteFile(), e);
            }
        } else {
            LOG.warn("Failed to load config {}, writing defaults", file.getAbsoluteFile());
            mapper.writeValue(file, config);
        }
        return config;
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    private void start() throws IOException {
        // restore cache
        restoreSensorValues();

        // start REST API
        pmRestApiHandler.start();

        // schedule immediate job for instant feedback
        executor.submit(() -> runDownloadAndProcess(0));

        // schedule periodic job
        Instant now = Instant.now();
        long initialDelay = 300L - (now.getEpochSecond() % 300L);
        executor.scheduleAtFixedRate(() -> runDownloadAndProcess(2), initialDelay, 300L, TimeUnit.SECONDS);
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
            List<SensorValue> values = objectMapper.readValue(fos, new TypeReference<List<SensorValue>>() {
            });
            values.forEach(v -> sensorValueMap.put(v.id, v));
            LOG.info("Restored {} sensor values from cache", values.size());
        } catch (Throwable e) {
            LOG.warn("Could not restore sensor values", e);
        }
    }

    @SuppressWarnings("FutureReturnValueIgnored")
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
        ZonedDateTime utcTime = ZonedDateTime.ofInstant(now, ZoneOffset.UTC);
        int minute = 5 * (utcTime.getMinute() / 5);
        utcTime = utcTime.withMinute(minute).truncatedTo(ChronoUnit.MINUTES);

        // create temporary name
        File tempDir = new File(config.intermediateDir);
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            LOG.warn("Failed to create directory {}", tempDir.getAbsolutePath());
        }

        // delete output files for this time-of-day
        String pngName = String.format(Locale.ROOT, "%02d%02d.png", utcTime.getHour(), utcTime.getMinute());
        for (RenderJob job : config.renderJobs) {
            File jobDir = new File(tempDir, job.getName());
            File outputFile = new File(jobDir, pngName);
            if (outputFile.exists()) {
                LOG.info("Deleting file {}", outputFile.getAbsolutePath());
                if (!outputFile.delete()) {
                    LOG.warn("Deletion failed");
                }
            }
        }

        List<SensorValue> pmValues = new ArrayList<>();
        List<SensorValue> rhValues = new ArrayList<>();

        // download PM2.5 data from RIVM samenmeten
        try {
            // download lucht
            SamenmetenCsv csv = samenmetenDownloader.downloadDataFromFile("lucht");
            // save to intermediate file
            csv.write(new File("lucht.csv"));
            // add to collection
            List<SensorValue> samenmetenValues = convertSamenmeten(csv);
            pmValues.addAll(samenmetenValues);
            LOG.info("Collected {} PM2.5 values from samenmeten", samenmetenValues.size());
        } catch (IOException e) {
            LOG.warn("Failed to download samenmeten data: {}", e.getMessage());
        }

        // download data from sensor.community
        try {
            LOG.info("Retrieving dust data from sensor.community");
            List<DataPoint> dataPoints = sensComDataApi.downloadDust();

            // convert DataPoints to internal format
            List<SensorValue> scPmValues = convertDataPoints(dataPoints, "", "P2", 0);
            pmValues.addAll(scPmValues);
            List<SensorValue> scRhValues = convertDataPoints(dataPoints, "BME280", "humidity", 0);
            rhValues.addAll(scRhValues);
        } catch (IOException e) {
            LOG.warn("Failed to download sensor.community data: {}", e.getMessage());
        }

        // download PM2.5 from meetjestad
        try {
            MeetjestadData meetjestadData = meetjestadDownloader.download(now.minusSeconds(600));
            List<SensorValue> meetjestadValues = meetjestadData.toSensorValues();
            LOG.info("Collected {} PM2.5 values from meetjestad", meetjestadValues.size());
            pmValues.addAll(meetjestadValues);
        } catch (IOException e) {
            LOG.warn("Failed to download meetjestad data: {}", e.getMessage());
        }

        // filter invalid values
        pmValues = filterBySensorValue(pmValues);

        // update list of sensor values, expiring old data
        pmValues.forEach(v -> sensorValueMap.put(v.id, v));
        Instant expiryTime = now.minus(Duration.ofMinutes(config.keepingDurationMinutes));
        sensorValueMap.entrySet().removeIf(e -> e.getValue().time.isBefore(expiryTime));

        // update in REST service
        AirRestApi.updateSensorValues(sensorValueMap);

        // store cached value
        pmValues = new ArrayList<>(sensorValueMap.values());
        persistSensorValues(pmValues);

        // score by blocklist
        pmValues = scoreByBlockList(pmValues, 0);

        // score top percentile as implausible
        pmValues = scoreByPercentile(pmValues, 0.01, 1);

        // render all jobs
        for (RenderJob job : config.renderJobs) {
            File jobDir = new File(tempDir, job.getName());
            if (jobDir.mkdirs()) {
                LOG.info("Created directory {}", jobDir);
            }
            File outputFile = new File(config.outputPath, job.getName() + ".png");
            render(job, jobDir, pmValues, rhValues, utcTime.toInstant(), outputFile);
            // copy file for animation
            File animationFile = new File(jobDir, pngName);
            Files.copy(outputFile.toPath(), animationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private List<SensorValue> convertSamenmeten(SamenmetenCsv csv) {
        List<SensorValue> values = new ArrayList<>();
        for (SamenmetenCsvLuchtEntry entry : csv.getEntries()) {
            if (entry.hasValidLocation() && Double.isFinite(entry.getPm2_5())) {
                // we already have luftdaten/sensor.community data at high time resolution
                if (!entry.getProject().equals("Luftdaten")) {
                    SensorValue value = new SensorValue(EDataSource.SAMENMETEN, entry.getKitId(), entry.getLongitude(),
                            entry.getLatitude(), entry.getPm2_5(), entry.getTimestamp());
                    value.setPlausibility(entry.getPm25Qual());
                    values.add(value);
                }
                // update plausibility map
                plausibilityMap.put(entry.getKitId(), entry.getPm25Qual());
            }
        }
        return values;
    }

    private void render(RenderJob job, File jobDir, List<SensorValue> pmValues, List<SensorValue> rhValues,
                        Instant instant, File outputFile) {

        // apply bounding box
        pmValues = filterByBoundingBox(pmValues, job, 2.0);
        rhValues = filterByBoundingBox(rhValues, job, 1.0);

        // filter by source
        pmValues = filterBySource(pmValues, job.getSources());

        // calculate median humidity
        double medianRh = calculateMedian(rhValues);
        LOG.info("Median humidity = {} %", String.format(Locale.ROOT, "%.2f", medianRh));

        try {
            // create overlay
            File overlayFile = new File(jobDir, "overlay.png");
            renderDust(pmValues, overlayFile, colorMapper, job);

            // create composite from background image and overlay
            File baseMap = new File(job.getMapFile());
            File compositeFile = new File(jobDir, "composite.png");
            composite(config.compositeCmd, overlayFile, baseMap, compositeFile);

            // add timestamp to composite
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            String timestampText = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String stampText = String.format(Locale.ROOT, "%s%nRV: %.1f %%", timestampText, medianRh);
            timestamp(config.convertCmd, stampText, compositeFile, outputFile);
        } catch (IOException e) {
            LOG.trace("Caught IOException", e);
            LOG.warn("Caught IOException: {}", e.getMessage());
        }
    }

    private double calculateMedian(List<SensorValue> values) {
        List<SensorValue> copy = new ArrayList<>(values);
        copy.sort(Comparator.comparingDouble(v -> v.value));
        if (copy.isEmpty()) {
            return Double.NaN;
        }
        double left = copy.get((copy.size() - 1) / 2).value;
        double right = copy.get(copy.size() / 2).value;
        return (left + right) / 2;
    }

    /**
     * Filters sensor values by position according to a bounding box.
     *
     * @param values the sensor values
     * @param job    the render job
     * @param area   the area multiplier
     * @return values filtered by position
     */
    private List<SensorValue> filterByBoundingBox(List<SensorValue> values, RenderJob job, double area) {
        double rangeX = area * (job.getEast() - job.getWest());
        double rangeY = area * (job.getNorth() - job.getSouth());
        double midX = (job.getWest() + job.getEast()) / 2;
        double midY = (job.getNorth() + job.getSouth()) / 2;
        double minX = midX - rangeX / 2;
        double maxX = midX + rangeX / 2;
        double minY = midY - rangeY / 2;
        double maxY = midY + rangeY / 2;
        List<SensorValue> filtered = values.stream().filter(v -> (v.x > minX)).filter(v -> (v.x < maxX))
                .filter(v -> (v.y > minY)).filter(v -> (v.y < maxY)).collect(Collectors.toList());
        LOG.info("Filtered by bounding box: {} -> {}", values.size(), filtered.size());
        return filtered;
    }

    private List<SensorValue> filterBySource(List<SensorValue> values, Set<EDataSource> sources) {
        List<SensorValue> filtered = values.stream().filter(v -> sources.contains(v.source))
                .collect(Collectors.toList());
        LOG.info("Filtered by source: {} -> {}", values.size(), filtered.size());
        return filtered;
    }

    /**
     * Converts from the sensor.community datapoints format to internal format.
     */
    private List<SensorValue> convertDataPoints(List<DataPoint> dataPoints, String sensorType, String item,
                                                int defaultPlausibility) {
        List<SensorValue> values = new ArrayList<>();
        int numIndoor = 0;
        for (DataPoint dp : dataPoints) {
            Sensor sensor = dp.getSensor();
            if (sensorType.isEmpty() || sensorType.equals(sensor.getSensorType().getName())) {
                Location location = dp.getLocation();
                if (location.getIndoor() != 0) {
                    numIndoor++;
                    continue;
                }
                DataValue dataValue = dp.getSensorDataValues().getDataValue(item);
                if (dataValue != null) {
                    String id = Integer.toString(sensor.getId());
                    double x = location.getLongitude();
                    double y = location.getLatitude();
                    double v = dataValue.getValue();
                    SensorValue value = new SensorValue(EDataSource.SENSOR_COMMUNITY, id, x, y, v, dp.getTimestamp());
                    // tag with plausibility
                    String name = "LTD_" + dp.getLocation().getId();
                    int plausibility = plausibilityMap.getOrDefault(name, defaultPlausibility);
                    value.setPlausibility(plausibility);
                    values.add(value);
                }
            }
        }
        LOG.info("Collected {} sensors of type '{}' (ignored {} indoor)", values.size(), item, numIndoor);
        return values;
    }

    /**
     * Renders a JSON file to a PNG.
     *
     * @param sensorValues the data points
     * @param pngFile      the PNG file
     * @param colorMapper  the color mapper
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
        } catch (Exception e) {
            LOG.trace("Caught IOException", e);
            LOG.warn("Caught IOException: {}", e.getMessage());
        }
    }

}
