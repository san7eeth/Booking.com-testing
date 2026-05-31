package utils;

import base.BaseClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * ExcelUtils - Reads test data from Excel file using Apache POI.
 *
 * Excel file : src/test/resources/testdata/testdata.xlsx
 * Sheets     : SearchData, ValidationData
 *
 * Supports dynamic future dates using format: DYNAMIC+7 (today + 7 days)
 */
public class ExcelUtils {

    private static final Logger logger = LogManager.getLogger(ExcelUtils.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Reads the first data row from SearchData sheet.
     * Returns a HashMap with column names as keys.
     */
    public static HashMap<String, String> readSearchData() {
        String sheetName = BaseClass.config.getProperty("searchSheet", "SearchData");
        HashMap<String, String> data = readExcelSheet(sheetName, 1);
        resolveDynamicDates(data);
        logger.info("Search data read from Excel: {}", data);
        return data;
    }

    /**
     * Reads the first data row from ValidationData sheet.
     * Returns a HashMap with column names as keys.
     */
    public static HashMap<String, String> readValidationData() {
        String sheetName = BaseClass.config.getProperty("validationSheet", "ValidationData");
        HashMap<String, String> data = readExcelSheet(sheetName, 1);
        resolveDynamicDates(data);
        logger.info("Validation data read from Excel: {}", data);
        return data;
    }

    /**
     * Opens Excel file and reads one data row from the given sheet.
     *
     * @param sheetName  Name of the Excel sheet
     * @param rowIndex   Row number to read (1 = first data row after header)
     */
    private static HashMap<String, String> readExcelSheet(String sheetName, int rowIndex) {
        HashMap<String, String> rowData = new HashMap<>();
        String excelPath = BaseClass.config.getProperty("excelPath");

        try (FileInputStream fis = new FileInputStream(excelPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            Row headerRow = sheet.getRow(0);
            Row dataRow = sheet.getRow(rowIndex);

            if (headerRow == null || dataRow == null) {
                throw new RuntimeException("Header or data row missing in sheet: " + sheetName);
            }

            // Read each column using header name as key
            for (int col = 0; col < headerRow.getLastCellNum(); col++) {
                String columnName = getCellValue(headerRow.getCell(col));
                String cellValue = getCellValue(dataRow.getCell(col));
                if (!columnName.isEmpty()) {
                    rowData.put(columnName, cellValue);
                }
            }

            logger.info("Excel data loaded from sheet '{}' row {}", sheetName, rowIndex);

        } catch (Exception e) {
            logger.error("Failed to read Excel file: {}", e.getMessage());
            throw new RuntimeException("Could not read Excel test data from: " + excelPath, e);
        }

        return rowData;
    }

    /**
     * Converts CheckInDate and CheckOutDate to actual dates if they contain DYNAMIC keyword.
     */
    private static void resolveDynamicDates(Map<String, String> data) {
        if (data.containsKey("CheckInDate")) {
            data.put("CheckInDate", resolveDate(data.get("CheckInDate"), 7));
        }
        if (data.containsKey("CheckOutDate")) {
            data.put("CheckOutDate", resolveDate(data.get("CheckOutDate"), 12));
        }
    }

    /**
     * Resolves dynamic date values from Excel.
     *
     * Examples:
     *   "DYNAMIC+7"           -> today + 7 days
     *   "Dynamic Future Date" -> today + defaultOffset days
     *   "2026-06-15"          -> used as-is
     */
    public static String resolveDate(String dateValue, int defaultOffset) {
        if (dateValue == null || dateValue.trim().isEmpty()) {
            return LocalDate.now().plusDays(defaultOffset).format(DATE_FORMAT);
        }

        String value = dateValue.trim();

        if (value.toUpperCase().startsWith("DYNAMIC")) {
            if (value.matches("(?i)DYNAMIC\\+(\\d+)")) {
                int days = Integer.parseInt(value.replaceAll("(?i)DYNAMIC\\+", ""));
                return LocalDate.now().plusDays(days).format(DATE_FORMAT);
            }
            return LocalDate.now().plusDays(defaultOffset).format(DATE_FORMAT);
        }

        return value;
    }

    /**
     * Reads cell value as String regardless of cell type (text or number).
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().format(DATE_FORMAT);
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

    /**
     * Converts string value from Excel to integer with a default fallback.
     */
    public static int getIntValue(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
