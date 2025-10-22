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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {
    private final ExcelReaderService excelReaderService;
    private final InvoiceMapper invoiceMapper;
    private final ExternalInvoiceService externalInvoiceService;
    private final ExcelUtils excelUtils;

    private List<InvoiceDTO> readInvoices(Workbook workbook) throws IOException {
        List<ExcelRow> rows = excelReaderService.readExcelFile(workbook);
        return invoiceMapper.toInvoiceDTOS(rows);
    }

    private byte[] updateSecureId(Workbook workbook, List<InvoiceResult> results) {
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);
        int secureIdColumnIndex = -1;
        for (Cell cell : headerRow) {
            if (excelUtils.getCellStringValue(cell).equals("SECURE_ID")) {
                secureIdColumnIndex = cell.getColumnIndex();
                break;
            }
        }
        if (secureIdColumnIndex == -1) {
            log.error("Could not find 'SECURE_ID' column in the header.");
        } else {
            for (InvoiceResult result : results) {
                if (result.getResponse() != null) {
                    String newSecureId = result.getResponse().getSecureId();
                    List<Integer> rowNums = result.getInvoice().getRowNums();
                    for (int rowNum : rowNums) {
                        Row row = sheet.getRow(rowNum);
                        if (row != null) {
                            Cell cell = row.getCell(secureIdColumnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            cell.setCellValue(newSecureId);
                        }
                    }
                }
            }
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Failed to write workbook", e);
            e.printStackTrace();
        }
        return null;
    }

    public Mono<byte[]> adjustInvoices(InputStream inputStream) {
        return Mono.using(
                () -> new XSSFWorkbook(inputStream),
                workbook -> Mono.fromCallable(() -> readInvoices(workbook))
                        .subscribeOn(Schedulers.boundedElastic())
                        .doOnNext(invoices -> log.info("Read {} invoices, starting API calls...", invoices.size()))
                        .flatMap(invoices ->
                                Flux.fromIterable(invoices)
                                        .concatMap(externalInvoiceService::adjust)
                                        .collectList()
                                        .doOnNext(results -> log.info("Finished {} API calls. Writing updated Excel file...", results.size()))
                                        .mapNotNull(results -> updateSecureId(workbook, results))
                                        .subscribeOn(Schedulers.boundedElastic())
                        ),
                wb -> {
                    try {
                        wb.close();
                    } catch (IOException e) {
                    }
                }
        );
    }
}
