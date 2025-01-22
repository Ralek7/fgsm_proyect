package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExcelUtils {
    public static Map<String, String> readExcel(String filePath) {
        Map<String, String> dataVars = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Primera hoja
            int rowsLength = sheet.getLastRowNum(); // Total de filas

            for (int i = 1; i <= rowsLength; i++) { // Saltar encabezados
                Row row = sheet.getRow(i);

                if (row != null) {
                    Cell keyCell = row.getCell(0); // Columna 1: Clave
                    Cell valueCell = row.getCell(1); // Columna 2: Valor

                    if (keyCell != null && valueCell != null) {
                        String key = keyCell.getStringCellValue();
                        String value = valueCell.getStringCellValue();
                        dataVars.put(key, value);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataVars;
    }
}
