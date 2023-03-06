package com.smarttrading.app.datasupplier.utils;

import pro.xstore.api.message.codes.PERIOD_CODE;

import java.sql.Timestamp;
import java.time.Instant;

public class DataSupplierUtils {

    public static long getTimePeriodForReceiveCandles(PERIOD_CODE periodCode, long candles){
        return Timestamp.from(Instant.now()).getTime() - lastCandlesTimestamp(periodCode, candles);
    }

    public static long periodAsLong(PERIOD_CODE period_code){
        if(period_code == PERIOD_CODE.PERIOD_M5){
            return 5;
        } else if(period_code == PERIOD_CODE.PERIOD_M15){
            return 15;
        } else if(period_code == PERIOD_CODE.PERIOD_M30){
            return 30;
        } else if(period_code == PERIOD_CODE.PERIOD_H1){
            return 60;
        } else if(period_code == PERIOD_CODE.PERIOD_H4){
            return 240;
        } else if(period_code == PERIOD_CODE.PERIOD_D1){
            return 1440;
        } else if(period_code == PERIOD_CODE.PERIOD_W1){
            return 10080;
        }
        return 5;
    }

    public static long lastCandlesTimestamp(PERIOD_CODE period_code, long candles){
        if(period_code == PERIOD_CODE.PERIOD_M5){
            return candles * 5 * 60 * 1000;
        } else if(period_code == PERIOD_CODE.PERIOD_M15){
            return candles * 15 * 60 * 1000;
        } else if(period_code == PERIOD_CODE.PERIOD_M30){
            return candles * 30 * 60 * 1000;
        } else if(period_code == PERIOD_CODE.PERIOD_H1){
            return candles * 60 * 60 * 1000;
        } else if(period_code == PERIOD_CODE.PERIOD_H4){
            return candles * 240 * 60 * 1000;
        } else if(period_code == PERIOD_CODE.PERIOD_D1){
            return candles * 1440 * 60 * 1000;
        } else if(period_code == PERIOD_CODE.PERIOD_W1){
            return candles * 10080 * 60 * 1000;
        }
        return candles * 15 * 24 * 60 * 60 * 1000;
    }
}
