package com.smarttrading.app.xtb.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class XtbRequest {
    String symbol;
    String type;
    String tradeOperationCode;
    Double price;
    Double volume;

}
