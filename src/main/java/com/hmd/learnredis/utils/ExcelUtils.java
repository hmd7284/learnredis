package com.hmd.learnredis.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class ExcelUtils {
    public Object getCellValue(Cell cell) {
        if (cell == null) return null;
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return evaluateFormulaCell(cell);
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell))
                    return cell.getLocalDateTimeCellValue();
                return convertNumericCellValue(cell.getNumericCellValue());
            case STRING:
                return trimCellValue(cell.getStringCellValue());
            case _NONE:
            case BLANK:
            case ERROR:
            default:
                return null;
        }
    }

    private Object evaluateFormulaCell(Cell cell) {
        try {
            Workbook workbook = cell.getSheet().getWorkbook();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            CellValue cv = evaluator.evaluate(cell);
            if (cv == null) return null;
            switch (cv.getCellType()) {
                case BOOLEAN:
                    return cv.getBooleanValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell))
                        return cell.getLocalDateTimeCellValue();
                    return convertNumericCellValue(cv.getNumberValue());
                case STRING:
                    return trimCellValue(cv.getStringValue());
                case BLANK:
                case ERROR:
                default:
                    return null;
            }
        } catch (Exception e) {
            return getCachedFormulaValue(cell);
        }
    }

    private Object getCachedFormulaValue(Cell cell) {
        try {
            CellType cachedType = cell.getCachedFormulaResultType();
            switch (cachedType) {
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell))
                        return cell.getLocalDateTimeCellValue();
                    return convertNumericCellValue(cell.getNumericCellValue());
                case STRING:
                    return trimCellValue(cell.getStringCellValue());
                case BOOLEAN:
                    return cell.getBooleanCellValue();
                case BLANK:
                case ERROR:
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal convertNumericCellValue(double numericValue) {
        if (Double.isNaN(numericValue) || Double.isInfinite(numericValue))
            return null;
        String numericAsString = NumberToTextConverter.toText(numericValue);
        try {
            return new BigDecimal(numericAsString).stripTrailingZeros();
        } catch (Exception e) {
            return new BigDecimal(numericValue).stripTrailingZeros();
        }
    }

    public LocalDateTime getCellValueAsLocalDateTime(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC &&
                DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue();
        }
        return null;
    }

    private String trimCellValue(String s) {
        if (s == null) return null;
        return s.replace('\u00A0', ' ').trim();
    }

    public String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        DataFormatter dataFormatter = new DataFormatter();
        return trimCellValue(dataFormatter.formatCellValue(cell));
    }
}
