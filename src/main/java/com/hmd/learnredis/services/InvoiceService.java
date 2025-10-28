package com.hmd.learnredis.services;

import com.hmd.learnredis.dtos.InvoiceDTO;
import com.hmd.learnredis.dtos.InvoiceResult;
import com.hmd.learnredis.mappers.InvoiceMapper;
import com.hmd.learnredis.models.ExcelRow;
import com.hmd.learnredis.utils.ExcelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {
    private final ExcelReaderService excelReaderService;
    private final InvoiceMapper invoiceMapper;
    private final ExternalInvoiceService externalInvoiceService;
    private final ExcelUtils excelUtils;

    private List<InvoiceDTO> readInvoices(Workbook workbook) {
        List<ExcelRow> rows = excelReaderService.readExcelFile(workbook);
        return invoiceMapper.toInvoiceDTOS(rows);
    }

    private byte[] updateSecureId(Workbook workbook, List<InvoiceResult> results) {
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);
        int newColumnIndex = headerRow.getLastCellNum();
        Cell newCell = headerRow.createCell(newColumnIndex);
        newCell.setCellValue("RETURNED_SECURE_ID");
        for (InvoiceResult result : results) {
            if (result.getResponse() != null) {
                String newSecureId = result.getResponse().getSecureId();
                List<Integer> rowNums = result.getInvoice().getRowNums();
                for (int rowNum : rowNums) {
                    Row row = sheet.getRow(rowNum);
                    if (row != null) {
                        Cell cell = row.createCell(newColumnIndex);
                        cell.setCellValue(newSecureId);
                    }
                }
            }
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            baos.close();
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Failed to write excel file", e);
        }
        return null;
    }

    public byte[] adjustInvoices(InputStream inputStream, String token) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            List<InvoiceDTO> invoices = readInvoices(workbook);
            log.info("Read {} invoices, starting API calls...", invoices.size());

            List<InvoiceResult> results = new ArrayList<>();
            for (InvoiceDTO invoice : invoices) {
                InvoiceResult result = externalInvoiceService.adjust(invoice, token);
                results.add(result);
            }
            log.info("Finished {} API calls. Writing updated Excel file...", results.size());
            return updateSecureId(workbook, results);
        }
    }
}
