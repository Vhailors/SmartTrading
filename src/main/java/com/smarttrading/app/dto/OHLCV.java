package com.smarttrading.app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OHLCV {
    private double open; // open price
    private double close; // close price
    private double low; // low price
    private double high; // high price
    private double volume; // volume
}
