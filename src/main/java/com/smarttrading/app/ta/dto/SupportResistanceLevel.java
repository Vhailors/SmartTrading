package com.smarttrading.app.ta.dto;

import java.math.BigDecimal;

public class SupportResistanceLevel implements Comparable<SupportResistanceLevel> {
    private BigDecimal level;
    private int strength;

    public SupportResistanceLevel(BigDecimal level, int strength) {
        this.level = level;
        this.strength = strength;
    }

    public BigDecimal getLevel() {
        return level;
    }

    public void setLevel(BigDecimal level) {
        this.level = level;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    @Override
    public int compareTo(SupportResistanceLevel o) {
        return o.strength - this.strength;
    }
}
