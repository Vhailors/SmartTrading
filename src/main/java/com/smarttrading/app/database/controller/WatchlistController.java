package com.smarttrading.app.database.controller;


import com.smarttrading.app.database.entity.SymbolMapping;
import com.smarttrading.app.database.entity.Watchlist;
import com.smarttrading.app.database.service.WatchlistService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/watchlist")
@AllArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @PostMapping
    public ResponseEntity createSymbolMapping(@RequestBody List<Watchlist> watchlistList) {
        watchlistService.createWatchlist(watchlistList);
        return ResponseEntity.ok().build();

    }
}
