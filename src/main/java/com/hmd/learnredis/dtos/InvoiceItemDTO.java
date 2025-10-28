package com.hmd.learnredis.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class InvoiceItemDTO {
    @JsonProperty("item_line")
    private Long itemLine;

    @JsonProperty("item_code")
    @Size(max = 40, message = "item_code cannot exceed 40 characters")
    private String itemCode;

    @JsonProperty(value = "item_name", required = true)
    @NotBlank(message = "item_name is required")
    @Size(max = 300, message = "item_name cannot exceed 300 characters")
    private String itemName;

    @JsonProperty(value = "unit", required = true)
    @NotBlank(message = "unit is required")
    @Size(max = 20, message = "unit cannot exceed 20 characters")
    private String unit;

    @JsonProperty(value = "price", required = true)
    @NotNull(message = "price is required")
    private BigDecimal price;

    @JsonProperty(value = "quantity", required = true)
    @NotNull(message = "quantity is required")
    private Long quantity;

    @JsonProperty(value = "discount_rate", defaultValue = "0.0")
    private BigDecimal discountRate;

    @JsonProperty("tax_rate_code")
    private String taxRateCode;

    @JsonProperty("amount_tax")
    private BigDecimal amountTax;

    @JsonProperty(value = "amount_discount", defaultValue = "0.0")
    private BigDecimal amountDiscount;

    @JsonProperty("amount_befor_tax")
    private BigDecimal amountBeforeTax;

    @JsonProperty("amount_after_tax")
    private BigDecimal amountAfterTax;
}
