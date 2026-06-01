package utils;

import base.BaseClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ExcelUtils {

    private static final Logger logger = LogManager.getLogger(ExcelUtils.class);

    public static HashMap<String, String> readSearchData() {
        return readExcelRow(BaseClass.config.getProperty("searchSheet", "SearchData"), 1);
    }

    public static HashMap<String, String> readValidationData() {
        return readExcelRow(BaseClass.config.getProperty("validationSheet", "ValidationData"), 1);
    }

    private static HashMap<String, String> readExcelRow(String sheetName, int rowIndex) {
        HashMap<String, String> rowData = new HashMap<>();
        String excelPath = BaseClass.config.getProperty("excelPath");

        try (FileInputStream fis = new FileInputStream(excelPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) throw new RuntimeException("Sheet not found: " + sheetName);

            Row headerRow = sheet.getRow(0);
            Row dataRow = sheet.getRow(rowIndex);
            if (headerRow == null || dataRow == null) throw new RuntimeException("Missing header or data row");

            for (int col = 0; col < headerRow.getLastCellNum(); col++) {
                String columnName = getCellValue(headerRow.getCell(col));
                String cellValue = getCellValue(dataRow.getCell(col));
                if (columnName.equalsIgnoreCase("Destination") || columnName.equalsIgnoreCase("Adults")
                        || columnName.equalsIgnoreCase("Children") || columnName.equalsIgnoreCase("Rooms")
                        || columnName.equalsIgnoreCase("TestCase")) {
                    rowData.put(columnName, cellValue);
                }
            }
            logger.info("Excel data read from sheet: {}", sheetName);
        } catch (Exception e) {
            logger.error("Failed to read Excel: {}", e.getMessage());
            throw new RuntimeException("Could not read Excel from: " + excelPath, e);
        }

        return rowData;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    return new SimpleDateFormat("yyyy-MM-dd").format(date);
                }
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    public static int getIntValue(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}