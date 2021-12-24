package nl.bertriksikken.stofradar.samenmeten.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class SamenmetenCsvLuchtParser {

    public List<SamenmetenCsvLuchtEntry> parse(String data) {
        List<SamenmetenCsvLuchtEntry> entries = new ArrayList<>();
        try (Scanner scanner = new Scanner(data).useDelimiter(";")) {
            while (scanner.hasNext()) {
                String line = scanner.next();
                SamenmetenCsvLuchtEntry entry = SamenmetenCsvLuchtEntry.parse(line);
                if (entry != null) {
                    entries.add(entry);
                }
            }
            return entries;
        }
    }

}
