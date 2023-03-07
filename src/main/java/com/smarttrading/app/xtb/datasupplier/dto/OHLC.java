package com.smarttrading.app.xtb.datasupplier.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class OHLC {
    private BigDecimal open;
    private BigDecimal low;
    private BigDecimal high;
    private BigDecimal close;

    private BigDecimal price;
}
