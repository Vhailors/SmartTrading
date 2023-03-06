package com.smarttrading.app.datasupplier.dto;

import lombok.Builder;
import lombok.Data;
import pro.xstore.api.message.codes.PERIOD_CODE;

import java.util.List;


@Builder
@Data
public class Instrument {
    private String symbol;
    private List<OHLC> candles;
    private PERIOD_CODE timeFrame;
    private double price;
}
