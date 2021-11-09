package utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CsvReader {
    public static List<Map<String, Object>> getRecordFromCsv(String fileName) throws IOException , CsvValidationException {
        List<Map<String, Object>> records = new ArrayList<>();
        List<String> header = new ArrayList<>();
        CSVReader reader = new CSVReader(new FileReader(System.getProperty("user.dir") + PropertyUtils.getProperty("default.download.location") + "/" + fileName));
        int lineNumber = 0;
        String[] data;
        while ((data = reader.readNext()) != null) {
            int row = 0;
            if (lineNumber == 0) {
                for (String csvRecord : data) {
                    header.add(csvRecord);
                }
                lineNumber++;
            } else {
                HashMap<String, Object> record = new HashMap<>();
                for (String csvRecord : data) {
                    record.put(header.get(row), csvRecord);
                    row++;
                }
                records.add(record);
            }
        }
        return records;
    }
}
