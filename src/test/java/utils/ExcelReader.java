package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * if user provide filename than this method will search it in download and filelocation needs to be null
 * if user provide filelocation than filename will be ignored so user should define absolute path of file
 */
public class ExcelReader {

    public static List<Map<String, Object>> getRecords(String fileName,String fileLocation) {
        List<Map<String, Object>> records = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(getFile(fileLocation,fileName));
            Sheet firstSheet = workbook.getSheetAt(0);
            List<String> headers = new ArrayList<>();
            Row firstRow = firstSheet.getRow(0);
            for (int i = 0; i < firstRow.getLastCellNum(); i++) {
                headers.add(firstRow.getCell(i).getStringCellValue());
            }

            for (int i = 1; i < firstSheet.getLastRowNum(); i++) {
                Row row = firstSheet.getRow(i);
                HashMap<String, Object> record = new HashMap<>();
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                    switch (row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getCellType()) {
                        case NUMERIC:
                            record.put(headers.get(j), row.getCell(j).getNumericCellValue());
                            break;
                        case STRING:
                            record.put(headers.get(j), row.getCell(j).getStringCellValue());
                            break;
                        case BLANK:
                            record.put(headers.get(j), null);
                            break;
                    }
                }
                records.add(record);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
    public static FileInputStream getFile(String fileLocation , String fileName) throws FileNotFoundException {
        if(fileLocation == null){
            return new FileInputStream(System.getProperty("user.dir")+PropertyUtils.getProperty("default.download.location") + "/" + fileName);
        }
        else{
            return new FileInputStream(fileLocation);
        }
    }
}
