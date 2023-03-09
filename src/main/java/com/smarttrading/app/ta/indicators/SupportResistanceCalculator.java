package com.smarttrading.app.ta.indicators;

import com.smarttrading.app.ta.dto.SupportResistanceLevel;
import com.smarttrading.app.xtb.datasupplier.dto.OHLC;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class SupportResistanceCalculator {

    private List<OHLC> ohlcList;
    private int periods;

    public List<SupportResistanceLevel> calculateSupportLevels() {
        List<SupportResistanceLevel> supportLevels = new ArrayList<>();
        BigDecimal pivotPoint = calculatePivotPoint();
        BigDecimal range = calculateRange();
        BigDecimal firstSupport = pivotPoint.subtract(range.multiply(BigDecimal.valueOf(0.382)));
        BigDecimal secondSupport = pivotPoint.subtract(range.multiply(BigDecimal.valueOf(0.618)));
        BigDecimal thirdSupport = pivotPoint.subtract(range.multiply(BigDecimal.valueOf(1.0)));
        BigDecimal firstSMA = calculateSMA(periods, ohlcList.size() - 1);
        BigDecimal secondSMA = calculateSMA(periods * 2, ohlcList.size() - 1);
        BigDecimal thirdSMA = calculateSMA(periods * 3, ohlcList.size() - 1);
//        BigDecimal firstStoch = calculateStochastic(calculateStochasticK(ohlcList, periods), ohlcList.size() - 1);
//        BigDecimal secondStoch = calculateStochastic(calculateStochasticK(ohlcList, periods), ohlcList.size() - 2);
//        BigDecimal thirdStoch = calculateStochastic(calculateStochasticK(ohlcList, periods), ohlcList.size() - 3);
        supportLevels.add(new SupportResistanceLevel(firstSupport, calculateStrength(firstSupport, ohlcList, periods, firstSMA)));
        supportLevels.add(new SupportResistanceLevel(secondSupport, calculateStrength(secondSupport, ohlcList, periods * 2, secondSMA)));
        supportLevels.add(new SupportResistanceLevel(thirdSupport, calculateStrength(thirdSupport, ohlcList, periods * 3, thirdSMA)));
        Collections.sort(supportLevels);
        return supportLevels;
    }

    public List<SupportResistanceLevel> calculateResistanceLevels() {
        List<SupportResistanceLevel> resistanceLevels = new ArrayList<>();
        BigDecimal pivotPoint = calculatePivotPoint();
        BigDecimal range = calculateRange();
        BigDecimal firstResistance = pivotPoint.add(range.multiply(BigDecimal.valueOf(0.382)));
        BigDecimal secondResistance = pivotPoint.add(range.multiply(BigDecimal.valueOf(0.618)));
        BigDecimal thirdResistance = pivotPoint.add(range.multiply(BigDecimal.valueOf(1.0)));
        BigDecimal firstSMA = calculateSMA(periods, ohlcList.size() - 1);
        BigDecimal secondSMA = calculateSMA(periods * 2, ohlcList.size() - 1);
        BigDecimal thirdSMA = calculateSMA(periods * 3, ohlcList.size() - 1);
//        BigDecimal firstStoch = calculateStochastic(calculateStochasticK(ohlcList, periods), ohlcList.size() - 1);
//        BigDecimal secondStoch = calculateStochastic(calculateStochasticK(ohlcList, periods), ohlcList.size() - 2);
//        BigDecimal thirdStoch = calculateStochastic(calculateStochasticK(ohlcList, periods), ohlcList.size() - 3);
        resistanceLevels.add(new SupportResistanceLevel(firstResistance, calculateStrength(firstResistance, ohlcList, periods, firstSMA)));
        resistanceLevels.add(new SupportResistanceLevel(secondResistance, calculateStrength(secondResistance, ohlcList, periods * 2, secondSMA)));
        resistanceLevels.add(new SupportResistanceLevel(thirdResistance, calculateStrength(thirdResistance, ohlcList, periods * 3, thirdSMA)));
        Collections.sort(resistanceLevels);
        return resistanceLevels;
    }

    private BigDecimal calculatePivotPoint() {
        BigDecimal high = ohlcList.get(ohlcList.size() - 1).getHigh();
        BigDecimal low = ohlcList.get(ohlcList.size() - 1).getLow();
        BigDecimal close = ohlcList.get(ohlcList.size() - 1).getClose();
        return high.add(low).add(close).divide(BigDecimal.valueOf(3), BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal calculateRange() {
        BigDecimal high = ohlcList.get(ohlcList.size() - 1).getHigh();
        BigDecimal low = ohlcList.get(ohlcList.size() - 1).getLow();
        return high.subtract(low);
    }

    private BigDecimal calculateSMA(int periods, int endIndex) {
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = endIndex; i > endIndex - periods; i--) {
            sum = sum.add(ohlcList.get(i).getClose());
        }
        return sum.divide(BigDecimal.valueOf(periods), BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal calculateStochastic(int k, int endIndex) {
        // TODO: 07.03.2023 zbugowane na pewno
        BigDecimal latestClose = ohlcList.get(endIndex).getClose();
        BigDecimal lowestLow = ohlcList.get(endIndex - k + 1).getLow();
        BigDecimal highestHigh = ohlcList.get(endIndex - k + 1).getHigh();
        for (int i = endIndex - k + 2; i <= endIndex; i++) {
            if (ohlcList.get(i).getLow().compareTo(lowestLow) < 0) {
                lowestLow = ohlcList.get(i).getLow();
            }
            if (ohlcList.get(i).getHigh().compareTo(highestHigh) > 0) {
                highestHigh = ohlcList.get(i).getHigh();
            }
        }
        return latestClose.subtract(lowestLow).divide(highestHigh.subtract(lowestLow), BigDecimal.ROUND_HALF_UP);
    }

    private int calculateStrength(BigDecimal level, List<OHLC> ohlcList, int periods, BigDecimal SMA) {
        int count = 0;
        for (int i = ohlcList.size() - periods; i < ohlcList.size(); i++) {
            OHLC currentOHLC = ohlcList.get(i);
            if (currentOHLC.getHigh().compareTo(level) >= 0 && currentOHLC.getLow().compareTo(level) <= 0) {
                count++;
            }
        }
        return count;
    }

    private int calculateStochasticK(List<OHLC> ohlcList, int period) {
        List<BigDecimal> prices = ohlcList.stream()
                .skip(ohlcList.size() - period)
                .map(OHLC::getClose)
                .toList();

        BigDecimal lowestLow = prices.stream().min(BigDecimal::compareTo).get();
        BigDecimal highestHigh = prices.stream().max(BigDecimal::compareTo).get();

        BigDecimal currentPrice = prices.get(prices.size() - 1);
        BigDecimal position = currentPrice.subtract(lowestLow)
                .divide(highestHigh.subtract(lowestLow), 10, RoundingMode.HALF_UP);

        return position.multiply(BigDecimal.valueOf(100)).intValue();
    }
}
