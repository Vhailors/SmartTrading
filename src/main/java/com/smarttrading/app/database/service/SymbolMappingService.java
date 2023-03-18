package com.smarttrading.app.database.service;

import com.smarttrading.app.database.entity.SymbolMapping;
import com.smarttrading.app.database.repository.SymbolMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SymbolMappingService {

    private final SymbolMappingRepository symbolMappingRepository;

    public List<SymbolMapping> getAllSymbolMappings() {
        return symbolMappingRepository.findAll();
    }

    public Optional<SymbolMapping> getSymbolMappingById(Long id) {
        return symbolMappingRepository.findById(id);
    }

    public SymbolMapping createSymbolMapping(SymbolMapping symbolMapping) {
        return symbolMappingRepository.save(symbolMapping);
    }

    public Optional<SymbolMapping> updateSymbolMapping(Long id, SymbolMapping symbolMapping) {
        Optional<SymbolMapping> existingSymbolMapping = symbolMappingRepository.findById(id);
        if (existingSymbolMapping.isPresent()) {
            SymbolMapping updatedSymbolMapping = existingSymbolMapping.get();
            updatedSymbolMapping.setSymbol(symbolMapping.getSymbol());
            updatedSymbolMapping.setInvestingUrl(symbolMapping.getInvestingUrl());
            updatedSymbolMapping.setTradingViewUrl(symbolMapping.getTradingViewUrl());
            updatedSymbolMapping.setAlternateSymbol(symbolMapping.getAlternateSymbol());
            symbolMappingRepository.save(updatedSymbolMapping);
            return Optional.of(updatedSymbolMapping);
        } else {
            return Optional.empty();
        }
    }

    public boolean deleteSymbolMapping(Long id) {
        try {
            symbolMappingRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<SymbolMapping> getSymbolMappingBySymbol(String symbol){
        return symbolMappingRepository.getSymbolMappingBySymbol(symbol);
    }
}