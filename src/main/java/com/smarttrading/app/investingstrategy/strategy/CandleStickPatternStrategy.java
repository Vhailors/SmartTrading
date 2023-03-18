package com.smarttrading.app.investingstrategy.strategy;

import com.smarttrading.app.investingstrategy.dto.AllStrategyVariables;
import com.smarttrading.app.scrapper.dto.SupportResistanceZoneScrap;
import com.smarttrading.app.scrapper.service.InvestingScrapper;
import com.smarttrading.app.ta.dto.CandleStickPatternResponse;
import com.smarttrading.app.ta.dto.ElliotWaveResponse;
import com.smarttrading.app.ta.dto.Trend;
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
import java.util.Objects;


@Component
@Slf4j
public class CandleStickPatternStrategy extends BaseStrategy {


    private final XtbService xtbService;


    public CandleStickPatternStrategy(InvestingScrapper investingScrapper, TechnicalAnalysisService technicalAnalysisService, XtbService xtbService) {
        super(investingScrapper, technicalAnalysisService, xtbService);
        this.xtbService = xtbService;
    }

    public void executeBuy(String symbol, PERIOD_CODE timeFrame, SyncAPIConnector connector, AllStrategyVariables allStrategyVariables) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException, NoSuchFieldException {

        CandleStickPatternResponse candleStickPatternStrategy = allStrategyVariables.getCandleSticks();
        SupportResistanceZoneScrap scrappedSrZones = allStrategyVariables.getScrappedSrZones();
        double price = xtbService.getSymbolActualPrice(symbol, connector);

        if (isBuyConditionSatisfied(symbol, price, candleStickPatternStrategy, timeFrame)) {
            XtbRequest xtbRequest = buildXtbRequestBuy(symbol, price);
            log.info("Candlestick pattern BUY for symbol {}", symbol);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getS2())), Double.parseDouble(scrappedSrZones.getR1()), connector);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getS2())), Double.parseDouble(scrappedSrZones.getR2()), connector);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getS2())), Double.parseDouble(scrappedSrZones.getR3()), connector);
        }
    }

    public void executeSell(String symbol, PERIOD_CODE timeFrame, SyncAPIConnector connector, AllStrategyVariables allStrategyVariables) throws
            APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException, NoSuchFieldException {

        CandleStickPatternResponse candleStickPatternStrategy = allStrategyVariables.getCandleSticks();
        SupportResistanceZoneScrap scrappedSrZones = allStrategyVariables.getScrappedSrZones();
        double price = xtbService.getSymbolActualPrice(symbol, connector);

        if (isSellConditionSatisfied(symbol, price, candleStickPatternStrategy, timeFrame)) {
            XtbRequest xtbRequest = buildXtbRequestSell(symbol, price);
            log.info("Candlestick pattern SELL for symbol {}", symbol);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getR2())), Double.parseDouble(scrappedSrZones.getS1()), connector);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getR2())), Double.parseDouble(scrappedSrZones.getS2()), connector);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getR2())), Double.parseDouble(scrappedSrZones.getS3()), connector);
        }
    }


    private boolean isBuyConditionSatisfied(String symbol, double price, CandleStickPatternResponse candleStickPatternResponse, PERIOD_CODE periodCode) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, NoSuchFieldException, APICommandConstructionException {
        if (Objects.isNull(candleStickPatternResponse)) return false;
        if (candleStickPatternResponse.getDirection() == Trend.BEARISH) return false;
        if (!isCurrentPriceNearSupportZone(symbol, price, periodCode)) return false;
        if (!(isInvestingScrapperSummaryStrongBuy(symbol, periodCode))) return false;
//        if (!(isInvestingScrapperSummaryStrongBuy(symbol, periodCode) || (isInvestingScrapperSummaryBuy(symbol, periodCode)))) return false;
        return true;
    }

    private boolean isSellConditionSatisfied(String symbol, double price, CandleStickPatternResponse candleStickPatternResponse, PERIOD_CODE periodCode) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, NoSuchFieldException, APICommandConstructionException {
        if (Objects.isNull(candleStickPatternResponse)) return false;
        if (candleStickPatternResponse.getDirection() == Trend.BULLISH) return false;
        if (!isCurrentPriceNearResistanceZone(symbol, price, periodCode)) return false;
        if (!isInvestingScrapperSummaryStrongSell(symbol, periodCode)) return false;
//        if (!isInvestingScrapperSummaryStrongSell(symbol, periodCode) || (isInvestingScrapperSummarySell(symbol, periodCode))) return false;
        return true;
    }
}

