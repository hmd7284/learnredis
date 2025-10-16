package com.hmd.learnredis.mappers;

import com.hmd.learnredis.dtos.InvoiceDTO;
import com.hmd.learnredis.dtos.InvoiceItemDTO;
import com.hmd.learnredis.models.ExcelRow;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class InvoiceMapper {
    public List<InvoiceDTO> toInvoiceDTOS(List<ExcelRow> rows) {
        List<InvoiceDTO> invoices = new ArrayList<>();
        for (ExcelRow row : rows) {
            List<InvoiceItemDTO> invoiceItems = new ArrayList<>();
            invoiceItems.add(InvoiceItemDTO.builder()
                    .itemLine(row.getLong("ITEM_LINE"))
                    .itemCode(row.getString("ITEM_CODE"))
                    .itemName(row.getString("'ĐIỀUCHỈNHGIẢM'||B.ITEM_NAME"))
                    .unit(row.getString("UNIT"))
                    .price(row.getBigDecimal("PRICE"))
                    .quantity(row.getLong("QUANTITY"))
                    .discountRate(BigDecimal.valueOf(0))
                    .taxRateCode(row.getString("TAX_RATE_CODE"))
                    .amountTax(row.getBigDecimal("AMOUNT_TAX"))
                    .amountDiscount(BigDecimal.valueOf(0))
                    .amountBeforeTax(row.getBigDecimal("AMOUNT_BEFORE_TAX"))
                    .amountAfterTax(row.getBigDecimal("AMOUNT_AFTER_TAX"))
                    .build());
            InvoiceDTO invoice = InvoiceDTO.builder()
                    .sourceOrg(row.getString("SOURCE_ORG"))
                    .sourceSystem(row.getString("SOURCE_SYSTEM"))
                    .form(row.getString("FORM"))
                    .serial(row.getString("SERIAL"))
                    .sourceTransId(row.getString("ORDER_NO"))
                    .transDate(row.getString("TRANS_DATE"))
                    .paymentMethod(row.getString("PAYMENT_METHOD"))
                    .noteId(row.getString("NOTE_ID"))
                    .noteDate(row.getString("NOTE_DATE"))
                    .custOrgName(row.getString("ORG"))
                    .custCode(row.getString("ORG_CODE"))
                    .custName(row.getString("ORG_CUST_NAME"))
                    .custAddress(row.getString("ORG_ADDRESS"))
                    .custPhone(row.getString("ORG_PHONE"))
                    .custEmail(row.getString("ORG_EMAIL"))
                    .custTaxCode(row.getString("ORG_TAX_CODE"))
                    .custBankAccount(row.getString("ORG_BANK_ACCOUNT"))
                    .custBank(row.getString("ORG_BANK"))
                    .totalAmountBeforeTax(row.getBigDecimal("TOTAL_AMOUNT_BEFOR_TAX"))
                    .totalTaxCode(row.getBigDecimal("TOTAL_TAX_CODE"))
                    .totalAmountTax(row.getBigDecimal("TOTAL_AMOUNT_TAX"))
                    .totalAmountAfterTax(row.getBigDecimal("TOTAL_AMOUNT_AFTER_TAX"))
                    .total(row.getBigDecimal("TOTAL_AMOUNT"))
                    .calType(row.getString("CAL_TYPE"))
                    .orgCustIdentity(row.getString("ORG_CUST_IDENTITY"))
                    .custPassport(row.getString("CUST_PASSPORT"))
                    .custRelationBudget(row.getString("CUST_RELATION_BUDGET"))
                    .type(row.getString("TYPE"))
                    .items(invoiceItems)
                    .build();
            invoices.add(invoice);
        }
        return invoices;
    }
}
