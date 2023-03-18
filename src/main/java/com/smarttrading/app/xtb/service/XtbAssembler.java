package com.smarttrading.app.xtb.service;

import com.smarttrading.app.database.entity.Trade;
import com.smarttrading.app.investingstrategy.dto.Strategy;
import com.smarttrading.app.xtb.dto.TimeFrame;
import com.smarttrading.app.xtb.dto.XtbRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pro.xstore.api.message.codes.TRADE_OPERATION_CODE;
import pro.xstore.api.message.codes.TRADE_TRANSACTION_TYPE;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.records.TradeTransInfoRecord;

@Service
@AllArgsConstructor
public class XtbAssembler {

    public TradeTransInfoRecord mapRequestToTrade(XtbRequest xtbRequest, Double stoploss, Double takeProfit, String comment) {
        return new TradeTransInfoRecord(
                getOperationCode(xtbRequest.getTradeOperationCode()),
                getTransactionType(xtbRequest.getType()),
                xtbRequest.getPrice(),
                stoploss,
                takeProfit,
                xtbRequest.getSymbol(),
                xtbRequest.getVolume(),
                0L,
                comment,
                0L);
    }

    public TradeTransInfoRecord mapDatabaseTradeToXtbTrade(Trade trade, Double price) {
        return new TradeTransInfoRecord(
                getOperationCode(trade.getTradeOperationCode()),
                TRADE_TRANSACTION_TYPE.MODIFY,
                price,
                trade.getStopLoss(),
                trade.getProfit(),
                trade.getSymbol(),
                trade.getVolume(),
                0L,
                null,
                0L);
    }

    public String getTradesDirection(TradeRecord tradeRecord) {
        if (tradeRecord.getCmd() == 0L) {
            return "BUY";
        } else if (tradeRecord.getCmd() == 1L) {
            return "SELL";
        } else return "unknown";
    }

    private TRADE_TRANSACTION_TYPE getTransactionType(String type) {
        return switch (type) {
            case "OPEN" -> TRADE_TRANSACTION_TYPE.OPEN;
            case "CLOSE" -> TRADE_TRANSACTION_TYPE.CLOSE;
            case "DELETE" -> TRADE_TRANSACTION_TYPE.DELETE;
            default -> null;
        };
    }

    private TRADE_OPERATION_CODE getOperationCode(String type) {
        if (type.equals("BUY")) {
            return TRADE_OPERATION_CODE.BUY;
        } else if (type.equals("SELL"))
            return TRADE_OPERATION_CODE.SELL;
        return null;
    }

}
