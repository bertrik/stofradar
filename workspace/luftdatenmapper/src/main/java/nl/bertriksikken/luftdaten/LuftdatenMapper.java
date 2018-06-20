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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.luftdaten.api.ILuftdatenRestApi;
import nl.bertriksikken.luftdaten.api.LuftDatenDataApi;
import nl.bertriksikken.luftdaten.api.dto.DataPoints;
import nl.bertriksikken.luftdaten.render.ColorMapper;
import nl.bertriksikken.luftdaten.render.ColorPoint;
import nl.bertriksikken.luftdaten.render.Interpolator;

/**
 * Process the luftdaten JSON and produces a CSV with coordinates and weighted dust averages.
 * 
 * @author bertrik
 *
 */
public final class LuftdatenMapper {

    private static final Logger LOG = LoggerFactory.getLogger(LuftdatenMapper.class);

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private static final ColorPoint[] RANGE = new ColorPoint[] {
            new ColorPoint(0, new int[] { 0xFF, 0xFF, 0xFF, 0x00 }), // transparent white
            new ColorPoint(25, new int[] { 0x00, 0xFF, 0xFF, 0xC0 }), // cyan
            new ColorPoint(50, new int[] { 0xFF, 0xFF, 0x00, 0xC0 }), // yellow
            new ColorPoint(100, new int[] { 0xFF, 0x00, 0x00, 0xC0 }), // red
            new ColorPoint(200, new int[] { 0xFF, 0x00, 0xFF, 0xC0 }), // purple
    };

    private DataPoints filterBySensorId(DataPoints dataPoints, List<Integer> blacklist) {
        DataPoints dps = new DataPoints();
        dps.addAll(dataPoints.stream().filter(dp -> !blacklist.contains(dp.getSensor().getId()))
                .collect(Collectors.toList()));
        return dps;
    }

    private DataPoints filterBySensorType(DataPoints dataPoints, int sensorType) {
        DataPoints dps = new DataPoints();
        dps.addAll(dataPoints.stream().filter(dp -> (dp.getSensor().getSensorType().getId() == sensorType))
                .collect(Collectors.toList()));
        return dps;
    }

    public static void main(String[] args) throws IOException {
        LuftdatenMapper luftdatenMapper = new LuftdatenMapper();
        LuftdatenMapperConfig config = new LuftdatenMapperConfig();
        File configFile = new File("luftdatenmapper.properties");
        if (configFile.exists()) {
            LOG.info("Loading config from {}", configFile);
            config.load(new FileInputStream(configFile));
        } else {
            LOG.info("Saving config to {}", configFile);
            config.save(new FileOutputStream(configFile));
        }
        luftdatenMapper.run(config);
    }

    private void run(ILuftdatenMapperConfig config) throws IOException {
        executor.scheduleAtFixedRate(() -> downloadAndProcess(config), 0L, 300L, TimeUnit.SECONDS);
    }

    private void downloadAndProcess(ILuftdatenMapperConfig config) {
        // get timestamp
        Instant now = Instant.now();
        ZonedDateTime dt = ZonedDateTime.ofInstant(now, ZoneId.of("UTC"));
        int minute = 5 * (dt.get(ChronoField.MINUTE_OF_HOUR) / 5);
        dt = dt.withMinute(minute);

        // create temporary name
        File tempDir = new File(config.getIntermediateDir());
        tempDir.mkdirs();
        String fileName = String.format(Locale.US, "%04d%02d%02d_%02d%02d.json", dt.get(ChronoField.YEAR),
                dt.get(ChronoField.MONTH_OF_YEAR), dt.get(ChronoField.DAY_OF_MONTH), dt.get(ChronoField.HOUR_OF_DAY),
                dt.get(ChronoField.MINUTE_OF_HOUR));
        File jsonFile = new File(tempDir, fileName);
        File overlayFile = new File(tempDir, jsonFile.getName() + ".png");

        try {
            int[] dims = Stream.of(config.getOverlayGeometry().split("x")).mapToInt(Integer::parseInt).toArray();
            int width = dims[0];
            int height = dims[1];

            // download JSON
            downloadFile(jsonFile, config.getLuftdatenUrl(), config.getLuftdatenTimeout());

            // JSON to objects
            ObjectMapper mapper = new ObjectMapper();
            DataPoints dataPoints = mapper.readValue(jsonFile, DataPoints.class);
            DataPoints filtered = filterBySensorId(filterBySensorType(dataPoints, 14), config.getLuftdatenBlacklist());

            // create overlay
            ColorMapper colorMapper = new ColorMapper(RANGE);
            renderDust(filtered, overlayFile, width, height, colorMapper);

            // create composite from background image and overlay
            File baseMap = new File(config.getBaseMapPath());
            File compositeFile = new File(tempDir, "composite.png");
            compositeFile.getParentFile().mkdirs();
            composite(config.getCompositeCmd(), overlayFile, baseMap, compositeFile);

            // add timestamp to composite
            LocalDateTime localDateTime = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
            String timestampText = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            File outputFile = new File(config.getOutputPath());
            timestamp(config.getConvertCmd(), timestampText, compositeFile, outputFile);

        } catch (IOException e) {
            LOG.trace("Caught IOException", e);
            LOG.warn("Caught IOException: {}", e.getMessage());
        } finally {
            jsonFile.delete();
            overlayFile.delete();
        }
    }

    /**
     * Downloads a new JSON dust file if necessary.
     * 
     * @param downloadDir the directory to download to
     * @param url the URL to download from
     * @return the file
     * @throws IOException
     */
    private File downloadFile(File file, String url, int timeout) throws IOException {
        if (!file.exists()) {
            ILuftdatenRestApi restApi = LuftDatenDataApi.newRestClient(url, timeout);
            LuftDatenDataApi api = new LuftDatenDataApi(restApi);
            LOG.info("Downloading new dataset to {}", file);
            api.downloadDust(file);
        }
        return file;
    }

    /**
     * Renders a JSON file to a PNG.
     * 
     * @param dataPoints the data points
     * @param pngFile the PNG file
     * @param colorMapper the color mapper
     * @throws IOException
     */
    private void renderDust(DataPoints dataPoints, File pngFile, int width, int height, ColorMapper colorMapper)
            throws IOException {
        LOG.info("Rendering {} data points to {}", dataPoints.size(), pngFile);

        // interpolate over grid
        Interpolator interpolator = new Interpolator();
        double[][] field = interpolator.interpolate(dataPoints, new Coord(3.3, 53.7), new Coord(4.0, 3.0), width,
                height);

        // convert to color PNG
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = image.getRaster();
        colorMapper.map(field, raster);

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
    private void composite(String command, File overlay, File baseMap, File outFile) {
        LOG.info("Compositing {} over {} to {}", overlay, baseMap, outFile);

        List<String> arguments = new ArrayList<>();
        arguments.add(command);
        arguments.addAll(Arrays.asList("-compose", "over"));
        arguments.addAll(Arrays.asList("-geometry", "600x800"));
        arguments.add(overlay.getAbsolutePath());
        arguments.add(baseMap.getAbsolutePath());
        arguments.add(outFile.getAbsolutePath());
        ProcessBuilder pb = new ProcessBuilder(arguments);
        try {
            Process process = pb.start();
            int exitValue = process.waitFor();
            if (exitValue != 0) {
                InputStream is = process.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    LOG.info("line: {}", line);
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
        arguments.addAll(Arrays.asList("-stroke", "white"));
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
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    LOG.info("line: {}", line);
                }
            }
            LOG.info("Process ended with {}", exitValue);
        } catch (Exception e) {
            LOG.trace("Caught IOException", e);
            LOG.warn("Caught IOException: {}", e.getMessage());
        }
    }

}
