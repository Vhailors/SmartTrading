package com.smarttrading.app.database.entity;

import com.smarttrading.app.investingstrategy.dto.Strategy;
import com.smarttrading.app.xtb.dto.TimeFrame;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String symbol;

    @Column
    private String type;

    @Column
    private String tradeOperationCode;

    @Column
    private Double entryPrice;

    @Column
    private Double volume;

    @Column
    private LocalDate openDate;

    @Column
    private LocalDate closeDate;

    @Column
    private Long xtbOpenTime;

    @Column
    @Enumerated(EnumType.STRING)
    private Strategy strategy;

    @Column
    @Enumerated(EnumType.STRING)
    private TimeFrame timeframe;

    @Column
    private Double takeProfit;

    @Column
    private Double stopLoss;

    @Column
    private Double profit;

    @Column
    private Double trailingLoss;

    public Trade() {

    }
}
