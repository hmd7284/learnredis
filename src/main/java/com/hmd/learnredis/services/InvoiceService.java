package com.hmd.learnredis.services;

import com.hmd.learnredis.dtos.InvoiceDTO;
import com.hmd.learnredis.mappers.InvoiceMapper;
import com.hmd.learnredis.models.ExcelRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final ExcelReaderService excelReaderService;
    private final InvoiceMapper invoiceMapper;

    public List<InvoiceDTO> readInvoices(InputStream inputStream) throws Exception {
        List<ExcelRow> rows = excelReaderService.readExcelFile(inputStream);
        return invoiceMapper.toInvoiceDTOS(rows);
    }
}
