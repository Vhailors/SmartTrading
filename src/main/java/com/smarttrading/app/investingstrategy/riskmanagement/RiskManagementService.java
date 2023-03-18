package com.smarttrading.app.investingstrategy.riskmanagement;

import com.smarttrading.app.database.entity.Trade;
import com.smarttrading.app.xtb.service.XtbService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.response.APIErrorResponse;

import java.io.IOException;
import java.util.List;


@Service
@AllArgsConstructor
public class RiskManagementService {

    private final XtbService xtbService;

    public void updateTrailingStopForTrade(Trade trade) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        double newStopLoss = calculateStopLoss(trade);
        trade.setStopLoss(newStopLoss);
        xtbService.updateTrade(trade);
    }

    public void updateTrailingStopForTrade(TradeRecord tradeRecord) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        Trade trade = mapTradeRecord(tradeRecord);
        double newStopLoss = calculateStopLoss(trade);
        xtbService.updateTrade(tradeRecord, newStopLoss);
    }

    public void updateTS() throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        List<TradeRecord> trades = xtbService.getOngoingTrades();
        trades.forEach(x -> {
            try {
                updateTrailingStopForTrade(x);
            } catch (APIErrorResponse | APICommandConstructionException | APIReplyParseException | IOException |
                     APICommunicationException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private double calculateStopLoss(Trade trade) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        double stopLoss = 0;
        boolean isBuy = StringUtils.equals(trade.getTradeOperationCode(), "BUY");
        double currentPrice = xtbService.getSymbolActualPrice(trade.getSymbol());
        double priceDifference = isBuy ? currentPrice - trade.getEntryPrice() : trade.getEntryPrice() - currentPrice;
        double trailingAmount = priceDifference * trade.getTrailingLoss();
        double newStopLoss = isBuy ? currentPrice - trailingAmount : currentPrice + trailingAmount;

        if (isBuy && newStopLoss > trade.getStopLoss()) {
            stopLoss = newStopLoss;
        } else if (!isBuy && newStopLoss < trade.getStopLoss()) {
            stopLoss = newStopLoss;
        }
        return stopLoss;
    }

    private Trade mapTradeRecord(TradeRecord trade){
        return Trade.builder()
                .tradeOperationCode(getTradesDirection(trade))
                .volume(trade.getVolume())
                .symbol(trade.getSymbol())
                .entryPrice(trade.getOpen_price())
                .trailingLoss(0.12)
                .stopLoss(trade.getSl())
                .build();
    }

    public String getTradesDirection(TradeRecord tradeRecord) {
        if (tradeRecord.getCmd() == 0L) {
            return "BUY";
        } else if (tradeRecord.getCmd() == 1L) {
            return "SELL";
        } else return "unknown";
    }
}

