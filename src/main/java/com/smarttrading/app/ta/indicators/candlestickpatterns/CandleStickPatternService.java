package com.smarttrading.app.ta.indicators.candlestickpatterns;

import com.smarttrading.app.xtb.datasupplier.dto.OHLC;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CandleStickPatternService {

    public boolean isThreeWhiteSoldiers(List<OHLC> candleList) {
        if (candleList.size() < 3) {
            return false;
        }
        if (!isWhite(candleList.get(candleList.size() - 1)) ||
                !isWhite(candleList.get(candleList.size() - 2)) ||
                !isWhite(candleList.get(candleList.size() - 3))) {
            return false;
        }
        if (candleList.get(candleList.size() - 1).getOpen().compareTo(candleList.get(candleList.size() - 2).getClose()) <= 0 ||
                candleList.get(candleList.size() - 2).getOpen().compareTo(candleList.get(candleList.size() - 3).getClose()) <= 0) {
            return false;
        }
        if (candleList.get(candleList.size() - 1).getClose().compareTo(candleList.get(candleList.size() - 2).getHigh()) <= 0 ||
                candleList.get(candleList.size() - 2).getClose().compareTo(candleList.get(candleList.size() - 3).getHigh()) <= 0) {
            return false;
        }
        return true;
    }

    public boolean isThreeBlackCrows(List<OHLC> candleList) {
        if (candleList.size() < 3) {
            return false;
        }
        if (isWhite(candleList.get(candleList.size() - 1)) ||
                isWhite(candleList.get(candleList.size() - 2)) ||
                isWhite(candleList.get(candleList.size() - 3))) {
            return false;
        }
        if (candleList.get(candleList.size() - 1).getOpen().compareTo(candleList.get(candleList.size() - 2).getClose()) >= 0 ||
                candleList.get(candleList.size() - 2).getOpen().compareTo(candleList.get(candleList.size() - 3).getClose()) >= 0) {
            return false;
        }
        if (candleList.get(candleList.size() - 1).getClose().compareTo(candleList.get(candleList.size() - 2).getLow()) >= 0 ||
                candleList.get(candleList.size() - 2).getClose().compareTo(candleList.get(candleList.size() - 3).getLow()) >= 0) {
            return false;
        }
        return true;
    }

    public boolean isBullishEngulfing(List<OHLC> candleList) {
        if (candleList.size() < 2) {
            return false;
        }
        if (!isWhite(candleList.get(candleList.size() - 1))) {
            return false;
        }
        if (isWhite(candleList.get(candleList.size() - 2))) {
            return false;
        }
        if (candleList.get(candleList.size() - 1).getClose().compareTo(candleList.get(candleList.size() - 2).getOpen()) <= 0) {
            return false;
        }
        if (candleList.get(candleList.size() - 1).getOpen().compareTo(candleList.get(candleList.size() - 2).getClose()) >= 0) {
            return false;
        }
        return true;
    }

    public boolean isBearishEngulfing(List<OHLC> candleList) {
        if (candleList.size() < 2) {
            return false;
        }
        if (isWhite(candleList.get(candleList.size() - 1))) {
            return false;
        }
        if (!isWhite(candleList.get(candleList.size() - 2))) {
            return false;
        }
        if (candleList.get(candleList.size() - 1).getClose().compareTo(candleList.get(candleList.size() - 2).getOpen()) >= 0) {
            return false;
        }
        if (candleList.get(candleList.size() - 1).getOpen().compareTo(candleList.get(candleList.size() - 2).getClose()) <= 0) {
            return false;
        }
        return true;
    }

    public boolean isEveningStar(List<OHLC> candleList) {
        if (candleList.size() < 3) {
            return false;
        }
        if (isWhite(candleList.get(candleList.size() - 1))) {
            return false;
        }
        OHLC secondLastCandle = candleList.get(candleList.size() - 2);
        if (isWhite(secondLastCandle) || !isLong(secondLastCandle)) {
            return false;
        }
        OHLC thirdLastCandle = candleList.get(candleList.size() - 3);
        if (isWhite(thirdLastCandle) || thirdLastCandle.getOpen().compareTo(secondLastCandle.getClose()) >= 0) {
            return false;
        }
        BigDecimal firstCandleMidpoint = calculateMidpoint(candleList.get(candleList.size() - 3));
        if (thirdLastCandle.getClose().compareTo(firstCandleMidpoint) >= 0) {
            return false;
        }
        return true;
    }

    private boolean isLong(OHLC candle) {
        BigDecimal bodyLength = candle.getOpen().subtract(candle.getClose()).abs();
        BigDecimal fullLength = candle.getHigh().subtract(candle.getLow());
        return bodyLength.compareTo(fullLength.multiply(new BigDecimal(0.5))) >= 0;
    }

    private BigDecimal calculateMidpoint(OHLC candle) {
        return candle.getHigh().add(candle.getLow()).divide(new BigDecimal(2), RoundingMode.HALF_UP);
    }

    public boolean isMorningStar(List<OHLC> candleList) {
        if (candleList.size() < 3) {
            return false;
        }
        if (!isWhite(candleList.get(candleList.size() - 1))) {
            return false;
        }
        if (isWhite(candleList.get(candleList.size() - 3))) {
            return false;
        }
        if (isWhite(candleList.get(candleList.size() - 2))) {
            return false;
        }
        if (candleList.get(candleList.size() - 2).getClose().compareTo(candleList.get(candleList.size() - 3).getClose()) >= 0) {
            return false;
        }
        BigDecimal middleCandleClose = candleList.get(candleList.size() - 2).getOpen().add(candleList.get(candleList.size() - 2).getClose()).divide(new BigDecimal("2"));
        if (candleList.get(candleList.size() - 1).getOpen().compareTo(middleCandleClose) <= 0) {
            return false;
        }
        if (candleList.get(candleList.size() - 1).getClose().compareTo(candleList.get(candleList.size() - 3).getOpen()) >= 0) {
            return false;
        }
        return true;
    }

    private boolean isWhite(OHLC ohlc) {
        return ohlc.getClose().compareTo(ohlc.getOpen()) > 0;
    }
}
