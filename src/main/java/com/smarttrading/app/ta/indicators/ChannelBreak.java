package com.smarttrading.app.ta.indicators;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ChannelBreak {
    public void checkChannelAndPattern(List<BigDecimal> prices, BigDecimal upperChannelLine, BigDecimal lowerChannelLine) {
        BigDecimal lastPrice = prices.get(prices.size() - 1);
        BigDecimal prevPrice = prices.get(prices.size() - 2);
        boolean isAboveUpperLine = lastPrice.compareTo(upperChannelLine) > 0;
        boolean isBelowLowerLine = lastPrice.compareTo(lowerChannelLine) < 0;
        boolean isAbovePrevPrice = lastPrice.compareTo(prevPrice) > 0;
        boolean isBelowPrevPrice = lastPrice.compareTo(prevPrice) < 0;
        boolean isBreakingThrough = false;
        boolean isBearish = false;

        if ((isAboveUpperLine && prevPrice.compareTo(upperChannelLine) <= 0) || (isBelowLowerLine && prevPrice.compareTo(lowerChannelLine) >= 0)) {
            isBreakingThrough = true;
        }

        if (isBreakingThrough && isBelowPrevPrice && isAboveUpperLine) {
            isBearish = true;
        } else if (isBreakingThrough && isAbovePrevPrice && isBelowLowerLine) {
            isBearish = false;
        }

        System.out.println("Is above upper channel line: " + isAboveUpperLine);
        System.out.println("Is below lower channel line: " + isBelowLowerLine);
        System.out.println("Is breaking through channel line: " + isBreakingThrough);
        System.out.println("Is bearish: " + isBearish);
    }

}
