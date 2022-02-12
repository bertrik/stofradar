package nl.bertriksikken.stofradar.samenmeten.csv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 * Writer for samenmeten CSV lucht data.
 */
public final class SamenmetenCsvWriter {

    private static final Logger LOG = LoggerFactory.getLogger(SamenmetenCsvWriter.class);

    private final CsvMapper csvMapper;

    SamenmetenCsvWriter() {
        this.csvMapper = new CsvMapper();
    }

    public void write(File file, List<SamenmetenCsvLuchtEntry> entries) throws IOException {
        CsvSchema schema = csvMapper.schemaFor(SamenmetenCsvLuchtEntry.class).withHeader().withColumnSeparator(';');
        ObjectWriter csvWriter = csvMapper.writer(schema);
        if (file.delete()) {
            LOG.info("Overwriting existing file {}", file.getAbsolutePath());
        } else {
            LOG.info("Creating new file {}", file.getAbsolutePath());
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            csvWriter.writeValues(fos).writeAll(entries);
        }
    }

}
