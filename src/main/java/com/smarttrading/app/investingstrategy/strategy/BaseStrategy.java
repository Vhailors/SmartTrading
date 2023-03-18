package com.smarttrading.app.investingstrategy.strategy;


import com.smarttrading.app.database.service.DatabaseTradeService;
import com.smarttrading.app.scrapper.dto.SupportResistanceZoneScrap;
import com.smarttrading.app.scrapper.dto.TechnicalAnalysisDTO;
import com.smarttrading.app.scrapper.service.InvestingScrapper;
import com.smarttrading.app.investingstrategy.dto.AllStrategyVariables;
import com.smarttrading.app.ta.dto.CandleStickPatternResponse;
import com.smarttrading.app.ta.dto.ElliotWaveResponse;
import com.smarttrading.app.ta.dto.SupportResistanceLevel;
import com.smarttrading.app.ta.dto.Trend;
import com.smarttrading.app.ta.service.TechnicalAnalysisService;
import com.smarttrading.app.xtb.dto.XtbRequest;
import com.smarttrading.app.xtb.service.XtbService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.sync.SyncAPIConnector;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Component
@AllArgsConstructor
public abstract class BaseStrategy {

    public final InvestingScrapper investingScrapper;
    private final TechnicalAnalysisService technicalAnalysisService;

    @Autowired
    private final XtbService xtbService;

//    @Autowired
//    private DatabaseTradeService databaseTradeService;

    public AllStrategyVariables getAllStrategyVariables(PERIOD_CODE periodCode, String symbol, long candlesNum, boolean realValue, SyncAPIConnector connector) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException, NoSuchFieldException {
        TechnicalAnalysisDTO scrappedTa = investingScrapper.scrapeInvestingData(symbol, periodCode);
        SupportResistanceZoneScrap scrappedSrZones = investingScrapper.getSupportResistanceValues(symbol, periodCode);
        CandleStickPatternResponse candleSticks = technicalAnalysisService.detectCandleStickPattern(periodCode, symbol, candlesNum, realValue, connector);
        ElliotWaveResponse elliotWaves = technicalAnalysisService.getElliotWave(periodCode, symbol, candlesNum, realValue, connector);
//        Map<String, SupportResistanceLevel> calculatedSrZones = technicalAnalysisService.getSupportResistanceLevels(periodCode, symbol, candlesNum, realValue);

        return AllStrategyVariables.builder()
                .scrappedTa(scrappedTa)
                .scrappedSrZones(scrappedSrZones)
                .candleSticks(candleSticks)
                .elliotWaves(elliotWaves)
//                .calculatedSrZones(calculatedSrZones)
                .build();
    }

    public AllStrategyVariables getAllStrategyVariablesWithoutXtb(PERIOD_CODE periodCode, String symbol) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException, NoSuchFieldException {
        TechnicalAnalysisDTO scrappedTa = investingScrapper.scrapeInvestingData(symbol, periodCode);
        SupportResistanceZoneScrap scrappedSrZones = investingScrapper.getSupportResistanceValues(symbol, periodCode);

        return AllStrategyVariables.builder()
                .scrappedTa(scrappedTa)
                .scrappedSrZones(scrappedSrZones)
                .build();
    }



    public boolean isCurrentPriceNearSupportZone(String symbol, double currentPrice, PERIOD_CODE periodCode) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException, NoSuchFieldException {
        SupportResistanceZoneScrap scrappedSrZones = investingScrapper.getSupportResistanceValues(symbol, periodCode);
        return isValueInRangeSupport(currentPrice, Double.parseDouble(scrappedSrZones.getS1()), Double.parseDouble(scrappedSrZones.getR1()));
    }

    public boolean isCurrentPriceNearResistanceZone(String symbol, double currentPrice, PERIOD_CODE periodCode) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException, NoSuchFieldException {
        SupportResistanceZoneScrap scrappedSrZones = investingScrapper.getSupportResistanceValues(symbol, periodCode);
        return isValueInRangeResistance(currentPrice, Double.parseDouble(scrappedSrZones.getS1()), Double.parseDouble(scrappedSrZones.getR1()));
    }

    public boolean isInvestingScrapperSummaryStrongBuy(String symbol, PERIOD_CODE periodCode) throws IOException, NoSuchFieldException {
        return StringUtils.equalsIgnoreCase(investingScrapper.scrapeInvestingData(symbol, periodCode).getSummary(), "STRONG BUY");
    }

    public boolean isInvestingScrapperSummaryBuy(String symbol, PERIOD_CODE periodCode) throws IOException, NoSuchFieldException {
        return StringUtils.equalsIgnoreCase(investingScrapper.scrapeInvestingData(symbol, periodCode).getSummary(), "BUY");
    }

    public boolean isInvestingScrapperSummaryStrongSell(String symbol, PERIOD_CODE periodCode) throws IOException, NoSuchFieldException {
        return StringUtils.equalsIgnoreCase(investingScrapper.scrapeInvestingData(symbol, periodCode).getSummary(), "STRONG SELL");
    }

    public boolean isInvestingScrapperSummaryNeutral(String symbol, PERIOD_CODE periodCode) throws IOException, NoSuchFieldException {
        return StringUtils.equalsIgnoreCase(investingScrapper.scrapeInvestingData(symbol, periodCode).getSummary(), "NEUTRAL");
    }

    public boolean isInvestingScrapperSummarySell(String symbol, PERIOD_CODE periodCode) throws IOException, NoSuchFieldException {
        return StringUtils.equalsIgnoreCase(investingScrapper.scrapeInvestingData(symbol, periodCode).getSummary(), "SELL");
    }

    public boolean isInvestingMovingAveragesStrongBuy(String symbol, PERIOD_CODE periodCode) throws IOException, NoSuchFieldException {
        return StringUtils.equalsIgnoreCase(investingScrapper.scrapeInvestingData(symbol, periodCode).getMovingAverages(), "STRONG BUY");
    }

    public boolean isInvestingMovingAveragesBuy(String symbol, PERIOD_CODE periodCode) throws IOException, NoSuchFieldException {
        return StringUtils.equalsIgnoreCase(investingScrapper.scrapeInvestingData(symbol, periodCode).getMovingAverages(), "BUY");
    }

    public boolean isInvestingMovingAveragesSell(String symbol, PERIOD_CODE periodCode) throws IOException, NoSuchFieldException {
        return StringUtils.equalsIgnoreCase(investingScrapper.scrapeInvestingData(symbol, periodCode).getMovingAverages(), "SELL");
    }

    public boolean isInvestingMovingAveragesStrongSell(String symbol, PERIOD_CODE periodCode) throws IOException, NoSuchFieldException {
        return StringUtils.equalsIgnoreCase(investingScrapper.scrapeInvestingData(symbol, periodCode).getMovingAverages(), "STRONG SELL");
    }

    public boolean isInvestingMovingAveragesNeutral(String symbol, PERIOD_CODE periodCode) throws IOException, NoSuchFieldException {
        return StringUtils.equalsIgnoreCase(investingScrapper.scrapeInvestingData(symbol, periodCode).getMovingAverages(), "NEUTRAL");
    }

    public boolean isValueInRangeSupport(double a, double support, double resistance) {
        double distance = resistance - support;
        double twentyPercent = 0.2 * distance;
        double lowerBound = support;
        double upperBound = support + twentyPercent;
        return (a >= lowerBound && a <= upperBound);
    }

    public boolean isValueInRangeResistance(double a, double support, double resistance) {
        double distance = resistance - support;
        double twentyPercent = 0.2 * distance;
        double lowerBound = resistance - twentyPercent;
        double upperBound = resistance;
        return (a >= lowerBound && a <= upperBound);
    }

    public XtbRequest buildXtbRequestBuy(String symbol, double price) {
        return XtbRequest.builder()
                .symbol(symbol)
                .type("OPEN")
                .tradeOperationCode("BUY")
                .volume(0.01)
                .price(price)
                .build();
    }

    public XtbRequest buildXtbRequestSell(String symbol, double price) {
        return XtbRequest.builder()
                .symbol(symbol)
                .type("OPEN")
                .tradeOperationCode("SELL")
                .volume(0.01)
                .price(price)
                .build();
    }

    public XtbRequest buildXtbRequestClose(String symbol, double price) {
        return XtbRequest.builder()
                .symbol(symbol)
                .type("CLOSE")
                .tradeOperationCode("SELL")
                .volume(0.01)
                .price(price)
                .build();
    }


//    public void saveTrade(){
//        databaseTradeService.saveTrade(createTradeFromRequest())
//    }
//
//    public Trade createTradeFromRequest(XtbRequest request, Strategy strategy, TimeFrame timeFrame){
//        return Trade.builder()
//                .entryPrice()
//                .profit()
//                .closeDate()
//                .openDate()
//                .stopLoss()
//                .tradeOperationCode()
//                .timeframe(timeFrame)
//                .symbol()
//                .trailingLoss()
//                .volume()
//                .xtbOpenTime()
//                .strategy(strategy)
//                .takeProfit()
//                .build()
//    }

    public void executeBuy(String symbol, PERIOD_CODE timeFrame, SyncAPIConnector connector ) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException, NoSuchFieldException {
    }

    public void executeSell(String symbol, PERIOD_CODE timeFrame, SyncAPIConnector connector) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException, NoSuchFieldException {
    }

    public boolean isBuyConditionSatisfied(String symbol, double price, CandleStickPatternResponse candleStickPatternResponse, ElliotWaveResponse elliotWaveResponse, PERIOD_CODE periodCode) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, NoSuchFieldException, APICommandConstructionException {
        if (Objects.isNull(candleStickPatternResponse)) return false;
        if (candleStickPatternResponse.getDirection() == Trend.BEARISH) return false;
        if (!isCurrentPriceNearSupportZone(symbol, price, periodCode)) return false;
        if (Objects.isNull(elliotWaveResponse)) return false;
        if (elliotWaveResponse.getTrend() == Trend.BEARISH) return false;
        if (!isInvestingScrapperSummaryStrongBuy(symbol, periodCode)) return false;
        return true;
    }

    public boolean isSellConditionSatisfied(String symbol, double price, CandleStickPatternResponse candleStickPatternResponse, ElliotWaveResponse elliotWaveResponse, PERIOD_CODE periodCode) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, NoSuchFieldException, APICommandConstructionException {
        if (Objects.isNull(candleStickPatternResponse)) return false;
        if (candleStickPatternResponse.getDirection() == Trend.BULLISH) return false;
        if (!isCurrentPriceNearResistanceZone(symbol, price, periodCode)) return false;
        if (Objects.isNull(elliotWaveResponse)) return false;
        if (elliotWaveResponse.getTrend() == Trend.BULLISH) return false;
        if (!isInvestingScrapperSummaryStrongSell(symbol, periodCode)) return false;
        return true;
    }
}
