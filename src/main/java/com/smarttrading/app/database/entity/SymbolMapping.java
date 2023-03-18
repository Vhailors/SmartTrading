package com.smarttrading.app.database.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import jakarta.persistence.*;


@Entity
@Data
public class SymbolMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String symbol;
    @Column
    private String investingUrl;
    @Column
    private String tradingViewUrl;
    @Column
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String alternateSymbol;
}
