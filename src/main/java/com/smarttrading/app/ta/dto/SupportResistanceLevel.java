package com.smarttrading.app.ta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data

public class SupportResistanceLevel implements Comparable<SupportResistanceLevel> {
    private BigDecimal level;
    private int strength;
    @Override
    public int compareTo(SupportResistanceLevel o) {
        return o.strength - this.strength;
    }
}
