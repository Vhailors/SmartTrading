package com.smarttrading.app.investingstrategy.strategy;

import com.smarttrading.app.investingstrategy.dto.AllStrategyVariables;
import com.smarttrading.app.scrapper.dto.SupportResistanceZoneScrap;
import com.smarttrading.app.scrapper.service.InvestingScrapper;
import com.smarttrading.app.ta.dto.CandleStickPatternResponse;
import com.smarttrading.app.ta.service.TechnicalAnalysisService;
import com.smarttrading.app.xtb.dto.XtbRequest;
import com.smarttrading.app.xtb.service.XtbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.sync.SyncAPIConnector;

import java.io.IOException;

@Component
@Slf4j
public class Investing1mScalpingStrategy extends BaseStrategy {

    private final XtbService xtbService;

    public Investing1mScalpingStrategy(InvestingScrapper investingScrapper, TechnicalAnalysisService technicalAnalysisService, XtbService xtbService) {
        super(investingScrapper, technicalAnalysisService, xtbService);
        this.xtbService = xtbService;
    }

    public void executeBuy(String symbol, PERIOD_CODE timeFrame, SyncAPIConnector connector, AllStrategyVariables allStrategyVariables) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException, NoSuchFieldException {
        SupportResistanceZoneScrap scrappedSrZones = allStrategyVariables.getScrappedSrZones();
        double price = xtbService.getSymbolActualPrice(symbol, connector);

        if (isBuyConditionSatisfied(symbol, price, timeFrame)) {
            XtbRequest xtbRequest = buildXtbRequestBuy(symbol, price);
            log.info("Investing1mScalpingStrategy BUY for symbol {}", symbol);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getS2())), Double.parseDouble(scrappedSrZones.getR1()), connector);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getS2())), Double.parseDouble(scrappedSrZones.getR2()), connector);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getS2())), Double.parseDouble(scrappedSrZones.getR3()), connector);
        }
    }

    public void executeSell(String symbol, PERIOD_CODE timeFrame, SyncAPIConnector connector, AllStrategyVariables allStrategyVariables) throws
            APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException, NoSuchFieldException {
        SupportResistanceZoneScrap scrappedSrZones = allStrategyVariables.getScrappedSrZones();
        double price = xtbService.getSymbolActualPrice(symbol, connector);

        if (isSellConditionSatisfied(symbol, price, timeFrame)) {
            XtbRequest xtbRequest = buildXtbRequestSell(symbol, price);
            log.info("Investing1mScalpingStrategy SELL for symbol {}", symbol);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getR2())), Double.parseDouble(scrappedSrZones.getS1()), connector);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getR2())), Double.parseDouble(scrappedSrZones.getS2()), connector);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getR2())), Double.parseDouble(scrappedSrZones.getS3()), connector);
        }

    }

    public void executeClose(String symbol, PERIOD_CODE timeFrame, SyncAPIConnector connector) throws IOException, NoSuchFieldException, APIErrorResponse, APICommunicationException, APIReplyParseException, APICommandConstructionException {
        if(isInvestingScrapperSummaryNeutral(symbol, timeFrame)) {
            log.info("Investing1mScalpingStrategy CLOSE for symbol {}", symbol);
            xtbService.closeSymbolTrades(symbol, connector);
        }
    }

    public boolean isBuyConditionSatisfied(String symbol, double price, PERIOD_CODE periodCode) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, NoSuchFieldException, APICommandConstructionException {
        if (isCurrentPriceNearSupportZone(symbol, price, periodCode)) {
            return isInvestingScrapperSummaryStrongBuy(symbol, periodCode) && isInvestingScrapperSummaryStrongBuy(symbol, PERIOD_CODE.PERIOD_H1);
        }
        return false;
    }

    public boolean isSellConditionSatisfied(String symbol, double price, PERIOD_CODE periodCode) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, NoSuchFieldException, APICommandConstructionException {
        if (!isCurrentPriceNearResistanceZone(symbol, price, periodCode)) return false;
        return isInvestingScrapperSummaryStrongSell(symbol, periodCode) && isInvestingScrapperSummaryStrongSell(symbol, PERIOD_CODE.PERIOD_H1);
    }
}