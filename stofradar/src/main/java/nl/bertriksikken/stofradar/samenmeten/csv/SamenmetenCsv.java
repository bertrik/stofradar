package nl.bertriksikken.stofradar.samenmeten.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public final class SamenmetenCsv {

    private static final CsvMapper CSV_MAPPER = new CsvMapper();
    private List<SamenmetenCsvLuchtEntry> entries = new ArrayList<>();

    private SamenmetenCsv() {
    }

    public static SamenmetenCsv parse(String text) throws JacksonException {
        // parse into lines
        List<String> lines = new ArrayList<>();
        try (Scanner scanner = new Scanner(text).useDelimiter(";")) {
            scanner.forEachRemaining(lines::add);
        }

        // parse each individual line
        SamenmetenCsv csv = new SamenmetenCsv();
        for (String line : lines) {
            List<String> fields = Stream.of(line.split(", ", -1)).map(String::trim).collect(Collectors.toList());
            SamenmetenCsvLuchtEntry entry = SamenmetenCsvLuchtEntry.from(fields);
            if (entry != null) {
                csv.add(entry);
            }
        }
        return csv;
    }

    private void add(SamenmetenCsvLuchtEntry entry) {
        entries.add(entry);
    }

    public void write(File file) throws IOException {
        CsvSchema schema = CSV_MAPPER.schemaFor(SamenmetenCsvLuchtEntry.class).withHeader().withNullValue("");
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            CSV_MAPPER.writer(schema).writeValues(writer).writeAll(entries);
        }
    }

    public List<SamenmetenCsvLuchtEntry> getEntries() {
        return List.copyOf(entries);
    }

}
