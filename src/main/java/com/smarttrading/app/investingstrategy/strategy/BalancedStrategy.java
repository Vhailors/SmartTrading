package com.smarttrading.app.investingstrategy.strategy;


import com.smarttrading.app.investingstrategy.dto.AllStrategyVariables;
import com.smarttrading.app.scrapper.dto.SupportResistanceZoneScrap;
import com.smarttrading.app.scrapper.dto.TechnicalAnalysisDTO;
import com.smarttrading.app.scrapper.service.InvestingScrapper;
import com.smarttrading.app.ta.dto.CandleStickPatternResponse;
import com.smarttrading.app.ta.dto.ElliotWaveResponse;
import com.smarttrading.app.ta.service.TechnicalAnalysisService;

import com.smarttrading.app.xtb.dto.XtbRequest;
import com.smarttrading.app.xtb.service.XtbService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.sync.SyncAPIConnector;

import java.io.IOException;
import java.util.List;

/*
 * Założenia (wymyślone przeze mnie):
 * Scalping oparty na świecach 15 min / 1h
 * Warunek wstępny - Zakup przy przejściu z BUY do STRONG BUY według scrapperów (porównanie na bazie)
 * Scheduler odpalony na sprawdzanie - co 10 minut
 * Wejście w pozycję podzielone na 3 wartości - ustawione kolejno na take profit ustawiony kolejno na warotściach support/resistance, SL na 2 poziomie
 * Wejście przy odległości pomiędzy S1 a R1 w 25% zależnie od kierunku
 * Risk management  - trailing loss 0.02
 * */
@Service
@Slf4j
public class BalancedStrategy extends BaseStrategy {

    private XtbService xtbService;

    public BalancedStrategy(InvestingScrapper investingScrapper, TechnicalAnalysisService technicalAnalysisService, XtbService xtbService) {
        super(investingScrapper, technicalAnalysisService, xtbService);
        this.xtbService = xtbService;
    }

    @Override
    public void executeBuy(String symbol, PERIOD_CODE timeFrame, SyncAPIConnector connector) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException, NoSuchFieldException {
        AllStrategyVariables allStrategyVariables = getAllStrategyVariables(timeFrame, symbol, 500, true, connector);
        CandleStickPatternResponse candleStickPatternStrategy = allStrategyVariables.getCandleSticks();
        SupportResistanceZoneScrap scrappedSrZones = allStrategyVariables.getScrappedSrZones();
        ElliotWaveResponse elliotWaveResponse = allStrategyVariables.getElliotWaves();
        double price = xtbService.getSymbolActualPrice(symbol, connector);

        if (isBuyConditionSatisfied(symbol, price, candleStickPatternStrategy, elliotWaveResponse, timeFrame)) {
            XtbRequest xtbRequest = buildXtbRequestBuy(symbol, price);
            log.info("Candlestick pattern BUY for symbol {}", symbol);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getS2())), Double.parseDouble(scrappedSrZones.getR1()), connector);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getS2())), Double.parseDouble(scrappedSrZones.getR2()), connector);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getS2())), Double.parseDouble(scrappedSrZones.getR3()), connector);
        }
    }

    @Override
    public void executeSell(String symbol, PERIOD_CODE timeFrame, SyncAPIConnector connector) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException, NoSuchFieldException {
        AllStrategyVariables allStrategyVariables = getAllStrategyVariables(timeFrame, symbol, 500, true, connector);
        CandleStickPatternResponse candleStickPatternStrategy = allStrategyVariables.getCandleSticks();
        SupportResistanceZoneScrap scrappedSrZones = allStrategyVariables.getScrappedSrZones();
        ElliotWaveResponse elliotWaveResponse = allStrategyVariables.getElliotWaves();
        double price = xtbService.getSymbolActualPrice(symbol, connector);

        if (isSellConditionSatisfied(symbol, price, candleStickPatternStrategy, elliotWaveResponse, timeFrame)) {
            XtbRequest xtbRequest = buildXtbRequestSell(symbol, price);
            log.info("Candlestick pattern SELL for symbol {}", symbol);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getR2())), Double.parseDouble(scrappedSrZones.getS1()), connector);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getR2())), Double.parseDouble(scrappedSrZones.getS2()), connector);
            xtbService.executeTrade(xtbRequest, (Double.parseDouble(scrappedSrZones.getR2())), Double.parseDouble(scrappedSrZones.getS3()), connector);
        }
    }
}
