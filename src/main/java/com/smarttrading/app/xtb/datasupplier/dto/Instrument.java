package com.smarttrading.app.xtb.datasupplier.dto;

import com.smarttrading.app.ai.dto.OHLCV;
import lombok.Builder;
import lombok.Data;
import pro.xstore.api.message.codes.PERIOD_CODE;

import java.util.List;
@Builder
@Data
public class Instrument {
    private String symbol;
    private List<OHLC> candles;

    private List<OHLCV> candlesDouble;
    private PERIOD_CODE timeFrame;
    private double price;
}
