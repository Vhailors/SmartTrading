package com.smarttrading.app.database.entity;


import com.smarttrading.app.investingstrategy.dto.Strategy;
import com.smarttrading.app.xtb.dto.TimeFrame;
import lombok.Data;
import pro.xstore.api.message.codes.PERIOD_CODE;

import jakarta.persistence.*;

@Entity
@Data
public class Watchlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String symbol;

    @Column
    @Enumerated(EnumType.STRING)
    private Strategy strategy;

    @Column
    @Enumerated(EnumType.STRING)
    private TimeFrame timeFrame;


}
