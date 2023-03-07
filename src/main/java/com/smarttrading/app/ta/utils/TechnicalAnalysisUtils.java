package com.smarttrading.app.ta.utils;

import com.smarttrading.app.xtb.datasupplier.dto.OHLC;

import java.util.List;

public class TechnicalAnalysisUtils {

    private static List<OHLC> getLastThreeCandles(List<OHLC> candles){
        return candles.subList(Math.max(candles.size() - 3, 0), candles.size());
    }
}
