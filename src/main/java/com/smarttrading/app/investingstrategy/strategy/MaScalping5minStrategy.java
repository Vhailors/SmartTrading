package com.smarttrading.app.investingstrategy.strategy;

import com.smarttrading.app.investingstrategy.dto.AllStrategyVariables;
import com.smarttrading.app.scrapper.dto.SupportResistanceZoneScrap;
import com.smarttrading.app.scrapper.service.InvestingScrapper;
import com.smarttrading.app.ta.dto.CandleStickPatternResponse;
import com.smarttrading.app.ta.dto.ElliotWaveResponse;
import com.smarttrading.app.ta.service.TechnicalAnalysisService;
import com.smarttrading.app.xtb.dto.XtbRequest;
import com.smarttrading.app.xtb.service.XtbService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.sync.SyncAPIConnector;

import java.io.IOException;

@Component
@Slf4j
public class MaScalping5minStrategy extends BaseStrategy {

    private final XtbService xtbService;

    public MaScalping5minStrategy(InvestingScrapper investingScrapper, TechnicalAnalysisService technicalAnalysisService, XtbService xtbService) {
        super(investingScrapper, technicalAnalysisService, xtbService);
        this.xtbService = xtbService;
    }

    public void executeBuy(String symbol, PERIOD_CODE timeFrame, SyncAPIConnector connector) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException, NoSuchFieldException {

        double price = xtbService.getSymbolActualPrice(symbol, connector);

        if (isBuyConditionSatisfied(symbol, timeFrame)) {
            XtbRequest xtbRequest = buildXtbRequestBuy(symbol, price);
            log.info("MaScalping5minStrategy BUY for symbol {}", symbol);
            xtbService.executeTrade(xtbRequest, 0.0, 0.0, connector);
            xtbService.executeTrade(xtbRequest, 0.0, 0.0, connector);
            xtbService.executeTrade(xtbRequest, 0.0, 0.0, connector);
        }
    }

    public void executeSell(String symbol, PERIOD_CODE timeFrame, SyncAPIConnector connector) throws
            APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException, NoSuchFieldException {

        double price = xtbService.getSymbolActualPrice(symbol, connector);

        if (isSellConditionSatisfied(symbol, timeFrame)) {
            XtbRequest xtbRequest = buildXtbRequestSell(symbol, price);
            log.info("MaScalping5minStrategy pattern SELL for symbol {}", symbol);
            xtbService.executeTrade(xtbRequest, 0.0, 0.0, connector);
            xtbService.executeTrade(xtbRequest, 0.0, 0.0, connector);
            xtbService.executeTrade(xtbRequest, 0.0, 0.0, connector);
        }

    }

    public void executeClose(String symbol, PERIOD_CODE timeFrame, SyncAPIConnector connector) throws IOException, NoSuchFieldException, APIErrorResponse, APICommunicationException, APIReplyParseException, APICommandConstructionException {
        if(isInvestingMovingAveragesNeutral(symbol, timeFrame)) {
            log.info("MaScalping5minStrategy CLOSE for symbol {}", symbol);
            double price = xtbService.getSymbolActualPrice(symbol, connector);
            XtbRequest xtbRequest = buildXtbRequestClose(symbol, price);
            xtbService.executeTrade(xtbRequest);
        }
    }

    public boolean isBuyConditionSatisfied(String symbol, PERIOD_CODE periodCode) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, NoSuchFieldException, APICommandConstructionException {
        return isInvestingMovingAveragesStrongBuy(symbol, periodCode);
    }

    public boolean isSellConditionSatisfied(String symbol, PERIOD_CODE periodCode) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, NoSuchFieldException, APICommandConstructionException {
        return isInvestingMovingAveragesStrongSell(symbol, periodCode);
    }
}