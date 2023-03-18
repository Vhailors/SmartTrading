package com.smarttrading.app.scheduler;


import com.smarttrading.app.database.entity.Watchlist;
import com.smarttrading.app.database.service.WatchlistService;
import com.smarttrading.app.investingstrategy.dto.AllStrategyVariables;
import com.smarttrading.app.investingstrategy.riskmanagement.RiskManagementService;
import com.smarttrading.app.investingstrategy.strategy.*;
import com.smarttrading.app.ta.indicators.candlestickpatterns.CandleStickPattern;
import com.smarttrading.app.ta.indicators.candlestickpatterns.CandleStickPatternService;
import com.smarttrading.app.xtb.dto.TimeFrame;
import com.smarttrading.app.xtb.service.XtbService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.sync.SyncAPIConnector;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Component
@Slf4j
public class StrategyChecker {

    private final WatchlistService watchlistService;
    private final BalancedStrategy balancedStrategy;
    private final CandleStickPatternStrategy candleStickPatternStrategy;

    private final MaScalping5minStrategy maScalping5minStrategy;
    private final Investing1mScalpingStrategy investing1mScalpingStrategy;

    private final RiskManagementService riskManagementService;
    private final XtbService xtbService;


    @Scheduled(fixedRate = 60000)
    private void executeStrategies() throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException, NoSuchFieldException {
        SyncAPIConnector connector = xtbService.init();
        log.info("ExecuteStrategies run...");
        List<Watchlist> watchlistList = watchlistService.getWatchlist();

        for (Watchlist watchlist : watchlistList) {
            PERIOD_CODE periodCode = mapStringToPERIOD_CODE(watchlist.getTimeFrame());
            AllStrategyVariables allStrategyVariables = candleStickPatternStrategy.getAllStrategyVariables(periodCode, watchlist.getSymbol(), 10, true, connector);
            AllStrategyVariables allStrategyVariablesWithoutXtb = investing1mScalpingStrategy.getAllStrategyVariablesWithoutXtb(periodCode, watchlist.getSymbol());
            switch (watchlist.getStrategy()) {
                case BALANCED_STRATEGY: {
//                    balancedStrategy.executeBuy(watchlist.getSymbol(), periodCode, connector);
//                    balancedStrategy.executeSell(watchlist.getSymbol(), periodCode, connector);
                    break;
                }
                case MA_SCALPING_5MIN_STRATEGY:
                    if(periodCode == PERIOD_CODE.PERIOD_M5) {
//                        maScalping5minStrategy.executeBuy(watchlist.getSymbol(), periodCode, connector);
//                        maScalping5minStrategy.executeSell(watchlist.getSymbol(), periodCode, connector);
//                        maScalping5minStrategy.executeClose(watchlist.getSymbol(), periodCode, connector);
                        break;
                    }
                case INVESTING_1M_SCALPING_STRATEGY:
                    if(periodCode == PERIOD_CODE.PERIOD_M1 || periodCode == PERIOD_CODE.PERIOD_M5 || periodCode == PERIOD_CODE.PERIOD_M15) {
                        investing1mScalpingStrategy.executeBuy(watchlist.getSymbol(), periodCode, connector, allStrategyVariablesWithoutXtb);
                        investing1mScalpingStrategy.executeSell(watchlist.getSymbol(), periodCode, connector, allStrategyVariablesWithoutXtb);
                        investing1mScalpingStrategy.executeClose(watchlist.getSymbol(), periodCode, connector);
                        break;
                    }
                case CANDLESTICK_PATTERN_STRATEGY: {
                    candleStickPatternStrategy.executeBuy(watchlist.getSymbol(), periodCode, connector, allStrategyVariables);
                    candleStickPatternStrategy.executeSell(watchlist.getSymbol(), periodCode, connector, allStrategyVariables);
                    break;
                }
            }
        }
        connector.close();
    }

//    @Scheduled(fixedRate = 300000)
    private void updateTrailingLoss() throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        log.info("updateTrailingLoss run...");
        List<TradeRecord> trades = xtbService.getOngoingTrades();
        trades.forEach(x -> {
            try {
                riskManagementService.updateTrailingStopForTrade(x);
            } catch (APIErrorResponse | APICommandConstructionException | APIReplyParseException | IOException |
                     APICommunicationException e) {
                throw new RuntimeException(e);
            }
        });

    }

        private PERIOD_CODE mapStringToPERIOD_CODE(TimeFrame timeFrame) {
        return switch (timeFrame) {
            case P_1M -> PERIOD_CODE.PERIOD_M1;
            case P_5M -> PERIOD_CODE.PERIOD_M5;
            case P_15M -> PERIOD_CODE.PERIOD_M15;
            case P_30M -> PERIOD_CODE.PERIOD_M30;
            case P_1h -> PERIOD_CODE.PERIOD_H1;
            case P_4h -> PERIOD_CODE.PERIOD_H4;
            case P_1d -> PERIOD_CODE.PERIOD_D1;
        };
    }
}
