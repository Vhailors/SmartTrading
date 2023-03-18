package com.smarttrading.app.xtb.service;

import com.smarttrading.app.database.entity.Trade;
import com.smarttrading.app.database.service.DatabaseTradeService;
import com.smarttrading.app.xtb.dto.XtbRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.xstore.api.message.codes.TRADE_OPERATION_CODE;
import pro.xstore.api.message.codes.TRADE_TRANSACTION_TYPE;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.command.TradeTransactionCommand;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.response.*;
import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.ServerData;
import pro.xstore.api.sync.SyncAPIConnector;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class XtbService {

    @Value("${xtb.login}")
    private String login;

    @Value("${xtb.password}")
    private String password;
    @Autowired
    private XtbAssembler xtbAssembler;

    public XtbService(XtbAssembler xtbAssembler) { // <= implicit injection
        this.xtbAssembler = xtbAssembler;
    }


    public SyncAPIConnector init() throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        Credentials credentials = new Credentials(login, password);
        SyncAPIConnector connector = new SyncAPIConnector(ServerData.ServerEnum.DEMO);
        LoginResponse loginResponse = APICommandFactory.executeLoginCommand(connector, credentials);
        return connector;
    }

    public void executeTrade(XtbRequest request) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        SyncAPIConnector connector = init();
        Double price = getSymbolActualPrice(request.getSymbol());
        request.setPrice(price);
        TradeTransactionCommand ttr = APICommandFactory.createTradeTransactionCommand(xtbAssembler.mapRequestToTrade(request, 0.0, 0.0, null));
        APICommandFactory.executeTradeTransactionCommand(connector, ttr);
        connector.close();
    }

    public void executeTrade(XtbRequest request, Double stopLoss, Double takeProfit) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        SyncAPIConnector connector = init();
        Double price = getSymbolActualPrice(request.getSymbol());
        request.setPrice(price);
        TradeTransactionCommand ttr = APICommandFactory.createTradeTransactionCommand(xtbAssembler.mapRequestToTrade(request, stopLoss, takeProfit, null));
        APICommandFactory.executeTradeTransactionCommand(connector, ttr);
        connector.close();

    }

    public void executeTrade(XtbRequest request, Double stopLoss, Double takeProfit, SyncAPIConnector connector) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        if(!isSymbolOpen(request.getSymbol())) {
            Double price = getSymbolActualPrice(request.getSymbol());
            request.setPrice(price);
            TradeTransactionCommand ttr = APICommandFactory.createTradeTransactionCommand(xtbAssembler.mapRequestToTrade(request, stopLoss, takeProfit, null));
            APICommandFactory.executeTradeTransactionCommand(connector, ttr);
        }

    }

    public Double getBalance() throws IOException, APIErrorResponse, APICommunicationException, APIReplyParseException, APICommandConstructionException {
        SyncAPIConnector connector = init();
        double balance = APICommandFactory.executeMarginLevelCommand(connector).getBalance();
        connector.close();
        return balance;
    }

    public List<TradeRecord> getOngoingTrades() throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        SyncAPIConnector connector = init();
        TradesResponse tradesResponse = APICommandFactory.executeTradesCommand(connector, true);
        connector.close();
        return tradesResponse.getTradeRecords();

    }

    public void closeAllTrades() throws APIErrorResponse, APICommunicationException, APIReplyParseException, APICommandConstructionException, IOException {
        SyncAPIConnector connector = init();
        TradesResponse tradesResponse = APICommandFactory.executeTradesCommand(connector, true);
        List<TradeRecord> tradeRecordList = tradesResponse.getTradeRecords();
        closeTrades(connector, tradeRecordList);
        connector.close();

    }

    public void closeSymbolTrades(String symbol, SyncAPIConnector connector) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        List<TradeRecord> trades = getOngoingTrades().stream().filter(x -> x.getSymbol().equals(symbol)).collect(Collectors.toList());
        if(!trades.isEmpty()) {
            closeTrades(connector, trades);
        }
    }

    public Double getSymbolActualPrice(String symbol) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        SyncAPIConnector connector = init();
        SymbolResponse symbolResponse = APICommandFactory.executeSymbolCommand(connector, symbol);
        connector.close();
        return symbolResponse.getSymbol().getAsk();
    }

    public Double getSymbolActualPrice(String symbol, SyncAPIConnector connector) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        SymbolResponse symbolResponse = APICommandFactory.executeSymbolCommand(connector, symbol);
        return symbolResponse.getSymbol().getAsk();
    }

    public Double getSymbolSpread(String symbol) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        SyncAPIConnector connector = init();
        SymbolResponse symbolResponse = APICommandFactory.executeSymbolCommand(connector, symbol);
        connector.close();
        return symbolResponse.getSymbol().getSpreadRaw();
    }

    public TradingHoursResponse getTradingHours() {
        // todo
        return null;
    }

    public void swapPositions(String symbol) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        SyncAPIConnector connector = init();
        List<TradeRecord> tradeRecords = getTradesBySymbol(symbol);
        Optional<TradeRecord> record = tradeRecords.stream().findAny();
        if (record.isPresent()) {
            String tradeDirection = xtbAssembler.getTradesDirection(record.get());
            tradeRecords.forEach(x -> {
                try {
                    swapPositionForTrade(x, tradeDirection);
                } catch (APIErrorResponse | APICommunicationException | IOException | APIReplyParseException |
                         APICommandConstructionException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        connector.close();
    }

    public void swapPositionForTrade(TradeRecord tradeRecord, String direction) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        SyncAPIConnector connector = init();
        if (direction.equals("BUY")) {
            closeTrade(tradeRecord);
            XtbRequest request = XtbRequest.builder()
                    .price(getSymbolActualPrice(tradeRecord.getSymbol()))
                    .volume(tradeRecord.getVolume())
                    .type("OPEN")
                    .tradeOperationCode("SELL")
                    .symbol(tradeRecord.getSymbol())
                    .build();
            TradeTransactionCommand ttr = APICommandFactory.createTradeTransactionCommand(xtbAssembler.mapRequestToTrade(request, 0.0, 0.0, null));
            APICommandFactory.executeTradeTransactionCommand(connector, ttr);
        } else if (direction.equals("SELL")) {
            closeTrade(tradeRecord);
            XtbRequest request = XtbRequest.builder()
                    .price(getSymbolActualPrice(tradeRecord.getSymbol()))
                    .volume(tradeRecord.getVolume())
                    .type("OPEN")
                    .tradeOperationCode("BUY")
                    .symbol(tradeRecord.getSymbol())
                    .build();
            TradeTransactionCommand ttr = APICommandFactory.createTradeTransactionCommand(xtbAssembler.mapRequestToTrade(request, 0.0, 0.0, null));
            APICommandFactory.executeTradeTransactionCommand(connector, ttr);
            connector.close();

        }
    }

    public void updateTrade(Trade trade) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        SyncAPIConnector connector = init();
        Optional<TradeRecord> tradeRecord = getTradeRecordByXtbDate(trade);
        if (tradeRecord.isPresent()) {
            Double price = getSymbolActualPrice(trade.getSymbol());
            TradeTransactionCommand ttr = APICommandFactory.createTradeTransactionCommand(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.MODIFY, price, tradeRecord.get().getSl(), 0.0, tradeRecord.get().getSymbol(), tradeRecord.get().getVolume(), tradeRecord.get().getOrder(), null, 0L);
            APICommandFactory.executeTradeTransactionCommand(connector, ttr);
        }
        connector.close();

    }

    public void updateTrade(TradeRecord tradeRecord, double stopLoss) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        SyncAPIConnector connector = init();
        Double price = getSymbolActualPrice(tradeRecord.getSymbol(), connector);
        stopLoss = alignDecimalPlaces(stopLoss, price);
        TradeTransactionCommand ttr = APICommandFactory.createTradeTransactionCommand(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.MODIFY, price, stopLoss, 0.0, tradeRecord.getSymbol(), tradeRecord.getVolume(), tradeRecord.getOrder(), null, 0L);
        APICommandFactory.executeTradeTransactionCommand(connector, ttr);
        connector.close();

    }

    public double alignDecimalPlaces(double a, double b) {
        int decimalPlacesA = getDecimalPlaces(a);
        int decimalPlacesB = getDecimalPlaces(b);
        int decimalPlacesDifference = decimalPlacesB - decimalPlacesA;
        double multiplier = Math.pow(10, decimalPlacesDifference);
        return Math.round(a * multiplier) / multiplier;
    }

    public int getDecimalPlaces(double number) {
        String[] parts = Double.toString(number).split("\\.");
        return parts.length > 1 ? parts[1].length() : 0;
    }

    private Optional<TradeRecord> getTradeRecordByXtbDate(Trade trade) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        return getOngoingTrades().stream().filter(x -> Objects.equals(x.getOpen_time(), trade.getXtbOpenTime())).findFirst();
    }

    private boolean isSymbolOpen(String symbol) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        try {
            return getOngoingTrades().stream().filter(x -> x.getSymbol().equals(symbol)).toList().size() >= 3;
        } catch (NullPointerException e){
            log.info("Nie można znaleźć obiektu: {}", symbol);
        }
        return false;
    }

    private void closeTrade(TradeRecord tradeRecord) throws APICommandConstructionException, APIErrorResponse, APICommunicationException, APIReplyParseException, IOException {
        SyncAPIConnector connector = init();
        TradeTransactionCommand ttr = APICommandFactory.createTradeTransactionCommand(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.CLOSE, tradeRecord.getClose_price(), 0.0, 0.0, tradeRecord.getSymbol(), tradeRecord.getVolume(), tradeRecord.getOrder(), null, 0L);
        APICommandFactory.executeTradeTransactionCommand(connector, ttr);
        connector.close();
    }

    private void closeTrades(SyncAPIConnector connector, List<TradeRecord> tradeRecords) throws APICommunicationException {
        tradeRecords.forEach(trade -> {
            try {
                closeTrade(trade);
            } catch (APICommandConstructionException | APIErrorResponse | APICommunicationException |
                     APIReplyParseException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        connector.close();

    }

    private List<TradeRecord> getTradesBySymbol(String symbol) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        SyncAPIConnector connector = init();
        TradesResponse tradesResponse = APICommandFactory.executeTradesCommand(connector, true);
        List<TradeRecord> tradeRecordList = tradesResponse.getTradeRecords();
        connector.close();
        return tradeRecordList.stream().filter(x -> x.getSymbol().equals(symbol)).collect(Collectors.toList());
    }
}
