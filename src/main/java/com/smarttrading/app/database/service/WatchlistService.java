package com.smarttrading.app.database.service;

import com.smarttrading.app.database.entity.Watchlist;
import com.smarttrading.app.database.repository.WatchlistRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class WatchlistService {
    private final WatchlistRepository watchlistRepository;

    public List<Watchlist> getWatchlist() {
        return watchlistRepository.findAll();
    }

    @Transactional
    public void createWatchlist(Watchlist watchlist) {
        watchlistRepository.save(watchlist);
    }

    public void createWatchlist(List<Watchlist> watchlist){
        watchlist.forEach(this::createWatchlist);
    }

}
