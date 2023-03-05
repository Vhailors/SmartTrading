package com.smarttrading.app.xtb.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class XtbRequest {
    String symbol;
    String type;
    String tradeOperationCode;
    Double price;
    Double volume;

}
