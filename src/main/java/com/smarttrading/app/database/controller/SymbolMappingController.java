package com.smarttrading.app.database.controller;


import com.smarttrading.app.database.entity.SymbolMapping;
import com.smarttrading.app.database.service.SymbolMappingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/symbol-mappings")
public class SymbolMappingController {

    private final SymbolMappingService symbolMappingService;
    @GetMapping
    public List<SymbolMapping> getAllSymbolMappings() {
        return symbolMappingService.getAllSymbolMappings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SymbolMapping> getSymbolMappingById(@PathVariable Long id) {
        Optional<SymbolMapping> symbolMapping = symbolMappingService.getSymbolMappingById(id);
        return symbolMapping.map(mapping -> new ResponseEntity<>(mapping, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public SymbolMapping createSymbolMapping(@RequestBody SymbolMapping symbolMapping) {
        return symbolMappingService.createSymbolMapping(symbolMapping);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SymbolMapping> updateSymbolMapping(@PathVariable Long id, @RequestBody SymbolMapping symbolMapping) {
        Optional<SymbolMapping> updatedSymbolMapping = symbolMappingService.updateSymbolMapping(id, symbolMapping);
        return updatedSymbolMapping.map(mapping -> new ResponseEntity<>(mapping, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSymbolMapping(@PathVariable Long id) {
        boolean result = symbolMappingService.deleteSymbolMapping(id);
        if (result) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<SymbolMapping> getSymbolMappingBySymbol(@PathVariable String symbol) {
        Optional<SymbolMapping> symbolMapping = symbolMappingService.getSymbolMappingBySymbol(symbol);
        return symbolMapping.map(mapping -> new ResponseEntity<>(mapping, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


}