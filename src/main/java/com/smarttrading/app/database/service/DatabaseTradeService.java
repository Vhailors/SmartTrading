package com.smarttrading.app.database.service;

import com.smarttrading.app.database.entity.Trade;
import com.smarttrading.app.database.repository.TradeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DatabaseTradeService {

    private final TradeRepository tradeRepository;

    public DatabaseTradeService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    public Trade saveTrade(Trade trade) {
        return tradeRepository.save(trade);
    }

    public Optional<Trade> getTradeById(Long id) {
        return tradeRepository.findById(id);
    }

    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }

    public void deleteTrade(Long id) {
        tradeRepository.deleteById(id);
    }

    public Trade updateTrade(Long id, Trade updatedTrade) {
        Optional<Trade> optionalTrade = tradeRepository.findById(id);
        if (optionalTrade.isPresent()) {
            Trade trade = optionalTrade.get();
            trade.setSymbol(updatedTrade.getSymbol());
            trade.setType(updatedTrade.getType());
            trade.setTradeOperationCode(updatedTrade.getTradeOperationCode());
            trade.setEntryPrice(updatedTrade.getEntryPrice());
            trade.setVolume(updatedTrade.getVolume());
            trade.setOpenDate(updatedTrade.getOpenDate());
            trade.setCloseDate(updatedTrade.getCloseDate());
            trade.setStrategy(updatedTrade.getStrategy());
            trade.setTimeframe(updatedTrade.getTimeframe());
            trade.setTakeProfit(updatedTrade.getTakeProfit());
            trade.setStopLoss(updatedTrade.getStopLoss());
            trade.setProfit(updatedTrade.getProfit());
            return tradeRepository.save(trade);
        }
        return null;
    }
}