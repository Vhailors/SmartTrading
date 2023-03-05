package com.smarttrading.app.xtb.serivce;

import com.smarttrading.app.xtb.dto.XtbRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.message.response.TradesResponse;
import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.ServerData;
import pro.xstore.api.sync.SyncAPIConnector;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class XtbService {

    @Value("${xtb.login}")
    private String login;

    @Value("${xtb.password}")
    private String password;

    // ZŁA PRAKtYKA!
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
        TradeTransactionCommand ttr = APICommandFactory.createTradeTransactionCommand(xtbAssembler.mapRequestToTrade(request, 0.0, 0.0, null));
        APICommandFactory.executeTradeTransactionCommand(connector, ttr);
    }

    public Double getBalance() throws IOException, APIErrorResponse, APICommunicationException, APIReplyParseException, APICommandConstructionException {
        SyncAPIConnector connector = init();
        return APICommandFactory.executeMarginLevelCommand(connector).getBalance();
    }

    public List<TradeRecord> getOngoingTrades() throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        SyncAPIConnector connector = init();
        TradesResponse tradesResponse = APICommandFactory.executeTradesCommand(connector, true);
        return tradesResponse.getTradeRecords();

    }

    public void closeAllTrades() throws APIErrorResponse, APICommunicationException, APIReplyParseException, APICommandConstructionException, IOException {
        SyncAPIConnector connector = init();
        TradesResponse tradesResponse = APICommandFactory.executeTradesCommand(connector, true);
        List<TradeRecord> tradeRecordList = tradesResponse.getTradeRecords();
        closeTrades(connector, tradeRecordList);
    }

    private void closeTrade(TradeRecord tradeRecord) throws APICommandConstructionException, APIErrorResponse, APICommunicationException, APIReplyParseException, IOException {
        SyncAPIConnector connector = init();
        TradeTransactionCommand ttr = APICommandFactory.createTradeTransactionCommand(TRADE_OPERATION_CODE.SELL, TRADE_TRANSACTION_TYPE.CLOSE, tradeRecord.getClose_price(), 0.0, 0.0, tradeRecord.getSymbol(), tradeRecord.getVolume(), tradeRecord.getOrder(), null, 0L);
        APICommandFactory.executeTradeTransactionCommand(connector, ttr);
    }

    private void closeTrades(SyncAPIConnector connector, List<TradeRecord> tradeRecords){
        tradeRecords.forEach(trade -> {
            try {
                closeTrade(trade);
            } catch (APICommandConstructionException | APIErrorResponse | APICommunicationException |
                     APIReplyParseException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}