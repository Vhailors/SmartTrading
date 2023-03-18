package com.smarttrading.app.xtb.datasupplier.utils;

import pro.xstore.api.message.codes.PERIOD_CODE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;

public class DataSupplierUtils {

    public static long getTimePeriodForReceiveCandles(PERIOD_CODE periodCode, long candles) {
        return Timestamp.from(Instant.now()).getTime() - lastCandlesTimestamp(periodCode, candles);
    }

    public static long periodAsLong(PERIOD_CODE period_code) {
        if (period_code == PERIOD_CODE.PERIOD_M1) {
            return 60;
        }else if (period_code == PERIOD_CODE.PERIOD_M5) {
            return 60*5;
        } else if (period_code == PERIOD_CODE.PERIOD_M15) {
            return 60*15;
        } else if (period_code == PERIOD_CODE.PERIOD_M30) {
            return 60*30;
        } else if (period_code == PERIOD_CODE.PERIOD_H1) {
            return 60*60;
        } else if (period_code == PERIOD_CODE.PERIOD_H4) {
            return 60*240;
        } else if (period_code == PERIOD_CODE.PERIOD_D1) {
            return 60*1440;
        } else if (period_code == PERIOD_CODE.PERIOD_W1) {
            return 60*10080;
        }
        return 5;
    }

    public static long lastCandlesTimestamp(PERIOD_CODE period_code, long candles) {
        if (period_code == PERIOD_CODE.PERIOD_M5) {
            return candles * 5 * 60 * 1000;
        } else if (period_code == PERIOD_CODE.PERIOD_M15) {
            return candles * 15 * 60 * 1000;
        } else if (period_code == PERIOD_CODE.PERIOD_M30) {
            return candles * 30 * 60 * 1000;
        } else if (period_code == PERIOD_CODE.PERIOD_H1) {
            return candles * 60 * 60 * 1000;
        } else if (period_code == PERIOD_CODE.PERIOD_H4) {
            return candles * 240 * 60 * 1000;
        } else if (period_code == PERIOD_CODE.PERIOD_D1) {
            return candles * 1440 * 60 * 1000;
        } else if (period_code == PERIOD_CODE.PERIOD_W1) {
            return candles * 10080 * 60 * 1000;
        }
        return candles * 15 * 24 * 60 * 60 * 1000;
    }


    public static int countDecimalPlaces(double num) {
        String str = Double.toString(num);
        int decimalPos = str.indexOf(".");

        if (decimalPos < 0) {
            return 0;
        }
        return str.length() - decimalPos - 1;
    }

    public static BigDecimal moveDecimalPointLeft(double num, int x) {
        double multiplier = Math.pow(10, -x);
        return new BigDecimal(num*multiplier).setScale(5, RoundingMode.HALF_UP).stripTrailingZeros();
    }



}
