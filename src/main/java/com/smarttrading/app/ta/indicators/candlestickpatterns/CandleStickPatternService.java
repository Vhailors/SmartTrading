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

    public boolean isBullishThreeLineStrike(List<OHLC> candles) {
        if (candles.size() < 4) {
            return false;
        }
        OHLC firstCandle = candles.get(candles.size() - 4);
        OHLC secondCandle = candles.get(candles.size() - 3);
        OHLC thirdCandle = candles.get(candles.size() - 2);
        OHLC fourthCandle = candles.get(candles.size() - 1);

        BigDecimal firstOpen = firstCandle.getOpen();
        BigDecimal firstClose = firstCandle.getClose();
        BigDecimal secondOpen = secondCandle.getOpen();
        BigDecimal secondClose = secondCandle.getClose();
        BigDecimal thirdOpen = thirdCandle.getOpen();
        BigDecimal thirdClose = thirdCandle.getClose();
        BigDecimal fourthOpen = fourthCandle.getOpen();
        BigDecimal fourthClose = fourthCandle.getClose();

        if (firstClose.compareTo(firstOpen) < 0 && secondClose.compareTo(secondOpen) < 0
                && thirdClose.compareTo(thirdOpen) < 0 && fourthClose.compareTo(fourthOpen) > 0) {
            BigDecimal firstBody = firstOpen.subtract(firstClose);
            BigDecimal secondBody = secondOpen.subtract(secondClose);
            BigDecimal thirdBody = thirdOpen.subtract(thirdClose);
            BigDecimal fourthBody = fourthClose.subtract(fourthOpen);
            BigDecimal minBody = firstBody.min(secondBody).min(thirdBody);
            return fourthBody.compareTo(minBody.multiply(new BigDecimal(3))) >= 0;
        }
        return false;
    }

    public boolean isBearishThreeLineStrike(List<OHLC> candles) {
        if (candles.size() < 4) {
            return false;
        }
        OHLC firstCandle = candles.get(candles.size() - 4);
        OHLC secondCandle = candles.get(candles.size() - 3);
        OHLC thirdCandle = candles.get(candles.size() - 2);
        OHLC fourthCandle = candles.get(candles.size() - 1);

        BigDecimal firstOpen = firstCandle.getOpen();
        BigDecimal firstClose = firstCandle.getClose();
        BigDecimal secondOpen = secondCandle.getOpen();
        BigDecimal secondClose = secondCandle.getClose();
        BigDecimal thirdOpen = thirdCandle.getOpen();
        BigDecimal thirdClose = thirdCandle.getClose();
        BigDecimal fourthOpen = fourthCandle.getOpen();
        BigDecimal fourthClose = fourthCandle.getClose();

        if (firstClose.compareTo(firstOpen) > 0 && secondClose.compareTo(secondOpen) > 0
                && thirdClose.compareTo(thirdOpen) > 0 && fourthClose.compareTo(fourthOpen) < 0) {
            BigDecimal firstBody = firstClose.subtract(firstOpen);
            BigDecimal secondBody = secondClose.subtract(secondOpen);
            BigDecimal thirdBody = thirdClose.subtract(thirdOpen);
            BigDecimal fourthBody = fourthOpen.subtract(fourthClose);
            BigDecimal minBody = firstBody.min(secondBody).min(thirdBody);
            return fourthBody.compareTo(minBody.multiply(new BigDecimal(3))) >= 0;
        }
        return false;
    }

    public boolean isConcealingBabySwallow(List<OHLC> candles) {
        if (candles.size() < 5) {
            return false;
        }
        OHLC firstCandle = candles.get(candles.size() - 5);
        OHLC secondCandle = candles.get(candles.size() - 4);
        OHLC thirdCandle = candles.get(candles.size() - 3);
        OHLC fourthCandle = candles.get(candles.size() - 2);
        OHLC fifthCandle = candles.get(candles.size() - 1);

        BigDecimal firstOpen = firstCandle.getOpen();
        BigDecimal firstClose = firstCandle.getClose();
        BigDecimal secondOpen = secondCandle.getOpen();
        BigDecimal secondClose = secondCandle.getClose();
        BigDecimal thirdOpen = thirdCandle.getOpen();
        BigDecimal thirdClose = thirdCandle.getClose();
        BigDecimal fourthOpen = fourthCandle.getOpen();
        BigDecimal fourthClose = fourthCandle.getClose();
        BigDecimal fifthOpen = fifthCandle.getOpen();
        BigDecimal fifthClose = fifthCandle.getClose();

        if (firstClose.compareTo(firstOpen) < 0 && secondClose.compareTo(secondOpen) > 0) {
            if (thirdClose.compareTo(thirdOpen) < 0 && thirdClose.compareTo(secondClose) < 0) {
                if (fourthClose.compareTo(fourthOpen) > 0 && fifthClose.compareTo(fifthOpen) > 0
                        && fourthClose.compareTo(thirdClose) > 0 && fifthOpen.compareTo(thirdOpen) < 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isBearishHaramiCross(List<OHLC> candles) {
        if (candles.size() < 5) {
            return false;
        }
        OHLC firstCandle = candles.get(candles.size() - 5);
        OHLC secondCandle = candles.get(candles.size() - 4);
        OHLC thirdCandle = candles.get(candles.size() - 3);
        OHLC fourthCandle = candles.get(candles.size() - 2);
        OHLC fifthCandle = candles.get(candles.size() - 1);

        BigDecimal firstOpen = firstCandle.getOpen();
        BigDecimal firstClose = firstCandle.getClose();
        BigDecimal secondOpen = secondCandle.getOpen();
        BigDecimal secondClose = secondCandle.getClose();
        BigDecimal thirdOpen = thirdCandle.getOpen();
        BigDecimal thirdClose = thirdCandle.getClose();
        BigDecimal fourthOpen = fourthCandle.getOpen();
        BigDecimal fourthClose = fourthCandle.getClose();
        BigDecimal fifthOpen = fifthCandle.getOpen();
        BigDecimal fifthClose = fifthCandle.getClose();

        // Sprawdzamy, czy dwie pierwsze świece są białe
        if (firstClose.compareTo(firstOpen) > 0 && secondClose.compareTo(secondOpen) > 0) {
            // Sprawdzamy, czy trzecia świeca jest czarna i zamyka się powyżej otwarcia drugiej świecy
            if (thirdClose.compareTo(thirdOpen) < 0 && thirdClose.compareTo(secondOpen) > 0) {
                // Sprawdzamy, czy czwarta świeca jest krótka i otwiera się powyżej zamknięcia trzeciej świecy
                if (fourthClose.compareTo(fourthOpen) < 0 && fourthOpen.compareTo(thirdClose) < 0) {
                    // Sprawdzamy, czy piąta świeca jest czarna i zamyka się poniżej otwarcia czwartej świecy
                    if (fifthClose.compareTo(fifthOpen) < 0 && fifthClose.compareTo(fourthOpen) < 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isWhite(OHLC ohlc) {
        return ohlc.getClose().compareTo(ohlc.getOpen()) > 0;
    }
}
