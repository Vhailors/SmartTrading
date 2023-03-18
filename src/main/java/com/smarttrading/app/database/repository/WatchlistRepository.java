package com.smarttrading.app.database.repository;

import com.smarttrading.app.database.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

}
