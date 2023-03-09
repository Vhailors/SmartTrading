package com.smarttrading.app.ta.indicators;

import com.smarttrading.app.ta.dto.ElliotWavePhase;
import com.smarttrading.app.ta.dto.ElliotWaveResponse;
import com.smarttrading.app.ta.dto.Trend;
import com.smarttrading.app.xtb.datasupplier.dto.OHLC;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

@Component
public class ElliotPhaseWave {

    public ElliotWaveResponse getElliotWavePhaseAndTrend(List<OHLC> data) {
        ElliotWavePhase phase = identifyElliotWavePhase(data);
        Trend trend = identifyTrend(data);
        return new ElliotWaveResponse(phase, trend);
    }

    private ElliotWavePhase identifyElliotWavePhase(List<OHLC> data) {
        if (data.size() < 50) {
            return null;
        }

        BigDecimal highestPrice = data.stream().map(OHLC::getHigh).max(BigDecimal::compareTo).get();
        BigDecimal lowestPrice = data.stream().map(OHLC::getLow).min(BigDecimal::compareTo).get();
        BigDecimal averageClosePrice = data.stream().map(OHLC::getClose).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(data.size()), MathContext.DECIMAL128);

        BigDecimal threshold = BigDecimal.valueOf(0.03);
        BigDecimal range = highestPrice.subtract(lowestPrice);
        BigDecimal diff1 = data.get(data.size() - 1).getClose().subtract(data.get(data.size() - 2).getClose()).abs();
        BigDecimal diff2 = data.get(data.size() - 3).getClose().subtract(data.get(data.size() - 4).getClose()).abs();

        if (diff1.compareTo(threshold.multiply(range)) < 0 && diff2.compareTo(threshold.multiply(range)) < 0) {
            BigDecimal maxCorrection = BigDecimal.valueOf(0.618).multiply(range);
            BigDecimal correction = highestPrice.subtract(data.get(data.size() - 1).getClose());
            if (correction.compareTo(maxCorrection) > 0) {
                return ElliotWavePhase.ABC;
            } else {
                return ElliotWavePhase.X;
            }
        } else if (diff1.compareTo(threshold.multiply(range)) >= 0 && diff2.compareTo(threshold.multiply(range)) < 0) {
            return ElliotWavePhase.THREE;
        } else if (diff1.compareTo(threshold.multiply(range)) < 0 && diff2.compareTo(threshold.multiply(range)) >= 0) {
            return ElliotWavePhase.TWO;
        } else if (data.get(data.size() - 1).getClose().compareTo(averageClosePrice) < 0) {
            return ElliotWavePhase.FOUR;
        } else {
            return ElliotWavePhase.ONE;
        }
    }
    private Trend identifyTrend(List<OHLC> data) {
        if (data.size() < 20) {
            return null;
        }

        double averageClosePrice = data.stream()
                .map(OHLC::getClose)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(data.size()), MathContext.DECIMAL128).doubleValue();

        double close = data.get(data.size() - 1).getClose().doubleValue();

        if (close > averageClosePrice) {
            return Trend.BULLISH;
        } else {
            return Trend.BEARISH;
        }
    }
}