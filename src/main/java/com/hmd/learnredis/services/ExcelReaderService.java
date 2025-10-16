package com.hmd.learnredis.services;

import com.hmd.learnredis.models.ExcelRow;
import com.hmd.learnredis.utils.ExcelUtils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelReaderService {
    private final ExcelUtils excelUtils;

    public List<ExcelRow> readExcelFile(InputStream inputStream) throws Exception {
        List<ExcelRow> excelRows = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return excelRows;

        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(excelUtils.getCellStringValue(cell));
        }
        for (int i = 1; i <= sheet.getLastRowNum(); ++i) {
            Row dataRow = sheet.getRow(i);
            if (dataRow == null) continue;
            ExcelRow excelRow = new ExcelRow();
            for (int j = 0; j < headers.size(); ++j) {
                excelRow.put(headers.get(j), excelUtils.getCellValue(dataRow.getCell(j)));
            }
            excelRows.add(excelRow);
        }
        workbook.close();
        inputStream.close();
        return excelRows;
    }
}
