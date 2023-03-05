package com.smarttrading.app.datasupplier.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Builder
@Data
public class Instrument {
    private String symbol;
    private List<OHLC> candles;
    private TimeFrame timeFrame;
    private BigDecimal value;
}
