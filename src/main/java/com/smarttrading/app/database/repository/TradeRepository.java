package com.smarttrading.app.database.repository;

import com.smarttrading.app.database.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
}