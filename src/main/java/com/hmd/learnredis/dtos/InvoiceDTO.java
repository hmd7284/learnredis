package com.hmd.learnredis.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class InvoiceDTO {
    @JsonIgnore
    private String invoiceId;

    @JsonProperty(value = "source_org", required = true)
    @NotBlank(message = "source_org is required")
    @Size(max = 20, message = "source_org cannot exceed 40 characters")
    private String sourceOrg;

    @JsonProperty(value = "source_system", required = true)
    @NotBlank(message = "source_system is required.")
    @Size(max = 40, message = "source_system cannot exceed 40 characters")
    private String sourceSystem;

    @JsonProperty("form")
    private String form;

    @JsonProperty("serial")
    @Size(max = 6, message = "serial cannot exceed 6 characters")
    private String serial;

    @JsonProperty("source_trans_id")
    @Size(max = 50, message = "source_trans_id cannot exceed 30 characters")
    private String sourceTransId;

    @JsonProperty(value = "trans_date", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String transDate;

    @JsonIgnore
    private String secureId;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("note_id")
    @Size(max = 20, message = "note_id cannot exceed 20 characters")
    private String noteId;

    @JsonProperty("note_date")
    private String noteDate;

    @JsonProperty("cust_org_name")
    @Size(max = 200, message = "cust_org_name cannot exceed 200 characters")
    private String custOrgName;

    @JsonProperty("cust_code")
    @Size(max = 30, message = "cust_code cannot exceed 30 characters")
    private String custCode;

    @JsonProperty("cust_name")
    @Size(max = 50, message = "cust_name cannot exceed 50 characters")
    private String custName;

    @JsonProperty("cust_address")
    @Size(max = 200, message = "cust_address cannot exceed 200 characters")
    private String custAddress;

    @JsonProperty("cust_phone")
    @Size(max = 12, message = "cust_phone cannot exceed 12 characters")
    private String custPhone;

    @JsonProperty("cust_email")
    @Size(max = 50, message = "cust_email cannot exceed 50 characters")
    private String custEmail;

    @JsonProperty("cust_tax_code")
    @Size(max = 16, message = "cust_tax_code cannot exceed 16 characters")
    private String custTaxCode;

    @JsonProperty("cust_bank_account")
    @Size(max = 20, message = "cust_bank_account cannot exceed 20 characters")
    private String custBankAccount;

    @JsonProperty("cust_bank")
    @Size(max = 50, message = "cust_bank cannot exceed 50 characters")
    private String custBank;

    @JsonProperty("total_amount_befor_tax")
    private BigDecimal totalAmountBeforeTax;

    @JsonProperty("total_tax_code")
    private BigDecimal totalTaxCode;

    @JsonProperty("total_amount_tax")
    private BigDecimal totalAmountTax;

    @JsonProperty("total_amount_after_tax")
    private BigDecimal totalAmountAfterTax;

    @JsonProperty("a.total")
    private BigDecimal total;

    @JsonProperty("cal_type")
    private String calType;

    @JsonProperty("org_cust_identity")
    @Size(max = 12, message = "org_cust_identity cannot exceed 12 characters")
    private String orgCustIdentity;

    @JsonProperty("cust_passport")
    @Size(max = 20, message = "cust_passport cannot exceed 20 characters")
    private String custPassport;

    @JsonProperty("cust_relation_budget")
    @Size(max = 7, message = "cust_relation_budget cannot exceed 7 characters")
    private String custRelationBudget;

    @JsonProperty("type")
    private String type;

    @JsonProperty("items")
    @Valid
    private List<InvoiceItemDTO> items;

    @JsonIgnore
    private List<Integer> rowNums;
}
