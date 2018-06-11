package nl.bertriksikken.luftdaten;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    private DataPoints filterBySensorType(DataPoints dataPoints, int sensorType) {
        DataPoints dps = new DataPoints();
        dps.addAll(dataPoints.stream().filter(dp -> (dp.getSensor().getSensorType().getId() == sensorType))
                .collect(Collectors.toList()));
        return dps;
    }

    public static void main(String[] args) throws IOException {
        LuftdatenMapper luftdatenMapper = new LuftdatenMapper();
        luftdatenMapper.run("http://api.luftdaten.info");
    }

    private void run(String url) throws IOException {
        File inputDir = new File("download");
        File doneDir = new File("processed");
        File outputDir = new File("image");

        if (inputDir.mkdirs() || doneDir.mkdirs() || outputDir.mkdirs()) {
            throw new IOException("Failed to create directories");
        }

        // schedule the download process
        executor.scheduleAtFixedRate(() -> downloadFiles(inputDir, url), 0L, 10, TimeUnit.SECONDS);

        // schedule the json -> png process
        executor.scheduleAtFixedRate(() -> processFiles(inputDir, doneDir, outputDir), 0L, 10L, TimeUnit.SECONDS);
    }

    private void downloadFiles(File downloadDir, String url) {
        ZonedDateTime dt = ZonedDateTime.now(ZoneId.of("UTC"));
        int minute = 5 * (dt.get(ChronoField.MINUTE_OF_HOUR) / 5);
        String fileName = String.format(Locale.US, "%04d%02d%02d_%02d%02d.json", dt.get(ChronoField.YEAR),
                dt.get(ChronoField.MONTH_OF_YEAR), dt.get(ChronoField.DAY_OF_MONTH), dt.get(ChronoField.HOUR_OF_DAY),
                minute);
        try {
            File file = new File(downloadDir, fileName);
            if (!file.exists()) {
                ILuftdatenRestApi restApi = LuftDatenDataApi.newRestClient(url, 3000);
                LuftDatenDataApi api = new LuftDatenDataApi(restApi);
                LOG.info("Downloading new dataset");
                api.downloadDust(file);
            }
        } catch (IOException e) {
            LOG.warn("Caught {}", e.getMessage());
        }
    }

    private void processFiles(File inputDir, File doneDir, File outputDir) {
        File[] files = inputDir.listFiles((f) -> filterByExtension(f, ".json"));
        for (File file : files) {
            try {
                File outFile = new File(outputDir, file.getName() + ".png");
                if (!outFile.exists()) {
                    LOG.info("Processing file {}", file);
                    processFile(file, outFile);
                }
            } catch (IOException e) {
                LOG.warn("Caught {}", e.getMessage());
            } catch (Exception e) {
                LOG.warn("Caught {}", e.getMessage());
            } finally {
                // move to done dir
                // LOG.info("Moving file {} to {}", file, doneDir);
                // file.renameTo(new File(doneDir, file.getName()));
            }
        }
    }

    private boolean filterByExtension(File file, String extension) {
        return file.getName().endsWith(extension);
    }

    private void processFile(File file, File outFile) throws IOException {
        int width = 60;
        int height = 80;

        // read file
        ObjectMapper mapper = new ObjectMapper();
        DataPoints dataPoints = mapper.readValue(file, DataPoints.class);
        DataPoints filtered = filterBySensorType(dataPoints, 14);

        // interpolate over grid
        Interpolator interpolator = new Interpolator();
        double[][] field = interpolator.interpolate(filtered, new Coord(3.3, 53.7), new Coord(4.0, 3.0), width, height);

        // convert to color PNG
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = image.getRaster();
        ColorMapper colorMapper = new ColorMapper(RANGE);
        colorMapper.map(field, raster);

        // save it
        LOG.info("Writing to {}", outFile);
        ImageIO.write(image, "png", outFile);
    }

}
