package com.smarttrading.app.database.repository;

import com.smarttrading.app.database.entity.SymbolMapping;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SymbolMappingRepository extends JpaRepository<SymbolMapping, Long> {

    Optional<SymbolMapping> getSymbolMappingBySymbol(String symbol);
}
