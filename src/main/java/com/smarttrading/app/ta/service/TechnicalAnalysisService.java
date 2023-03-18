package com.smarttrading.app.ta.service;


import com.smarttrading.app.ta.dto.ElliotWaveResponse;
import com.smarttrading.app.ta.dto.SupportResistanceLevel;
import com.smarttrading.app.ta.dto.CandleStickPatternResponse;
import com.smarttrading.app.ta.dto.Trend;
import com.smarttrading.app.ta.indicators.ElliotPhaseWave;
import com.smarttrading.app.ta.indicators.SupportResistanceCalculator;
import com.smarttrading.app.ta.indicators.candlestickpatterns.CandleStickPattern;
import com.smarttrading.app.ta.indicators.candlestickpatterns.CandleStickPatternService;
import com.smarttrading.app.xtb.datasupplier.dto.Instrument;
import com.smarttrading.app.xtb.datasupplier.dto.OHLC;
import com.smarttrading.app.xtb.datasupplier.service.XtbDataSupplierService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.sync.SyncAPIConnector;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class TechnicalAnalysisService {

    private final XtbDataSupplierService xtbDataSupplierService;
    private final CandleStickPatternService candleStickPatternService;

    private final ElliotPhaseWave elliotPhaseWave;

    public CandleStickPatternResponse detectCandleStickPattern(PERIOD_CODE periodCode, String symbol, long candlesNum, boolean realValue, SyncAPIConnector connector) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        Instrument instrumentData =  xtbDataSupplierService.getInstrumentData(periodCode, symbol, candlesNum, realValue, connector);
        if(Objects.nonNull(instrumentData)) {
            List<OHLC> candles = instrumentData.getCandles();

            if (candleStickPatternService.isBearishEngulfing(candles)) {
                return CandleStickPatternResponse.builder()
                        .pattern(CandleStickPattern.BEARISH_ENGULFING)
                        .direction(Trend.BEARISH).build();
            }

            if (candleStickPatternService.isBullishEngulfing(candles)) {
                return CandleStickPatternResponse.builder()
                        .pattern(CandleStickPattern.BULLISH_ENGULFING)
                        .direction(Trend.BULLISH).build();
            }

            if (candleStickPatternService.isThreeWhiteSoldiers(candles)) {
                return CandleStickPatternResponse.builder()
                        .pattern(CandleStickPattern.THREE_WHITE_SOLDIERS)
                        .direction(Trend.BULLISH).build();
            }

            if (candleStickPatternService.isThreeBlackCrows(candles)) {
                return CandleStickPatternResponse.builder()
                        .pattern(CandleStickPattern.THREE_BLACK_CROWS)
                        .direction(Trend.BEARISH).build();
            }

            if (candleStickPatternService.isEveningStar(candles)) {
                return CandleStickPatternResponse.builder()
                        .pattern(CandleStickPattern.EVENING_STAR)
                        .direction(Trend.BEARISH).build();
            }

            if (candleStickPatternService.isMorningStar(candles)) {
                return CandleStickPatternResponse.builder()
                        .pattern(CandleStickPattern.MORNING_STAR)
                        .direction(Trend.BULLISH).build();
            }

            if (candleStickPatternService.isBullishThreeLineStrike(candles)) {
                return CandleStickPatternResponse.builder()
                        .pattern(CandleStickPattern.BULLISH_THREE_LINE_STRIKE)
                        .direction(Trend.BULLISH).build();
            }

            if (candleStickPatternService.isBearishThreeLineStrike(candles)) {
                return CandleStickPatternResponse.builder()
                        .pattern(CandleStickPattern.BEARISH_THREE_LINE_STRIKE)
                        .direction(Trend.BEARISH).build();
            }

            if (candleStickPatternService.isConcealingBabySwallow(candles)) {
                return CandleStickPatternResponse.builder()
                        .pattern(CandleStickPattern.CONCEALING_BABY_SWALLOW)
                        .direction(Trend.BULLISH).build();
            }

            if (candleStickPatternService.isBearishHaramiCross(candles)) {
                return CandleStickPatternResponse.builder()
                        .pattern(CandleStickPattern.BEARISH_HARAMI_CROSS)
                        .direction(Trend.BEARISH).build();
            }
        }
       return null;
    }

    public ElliotWaveResponse getElliotWave(PERIOD_CODE periodCode, String symbol, long candlesNum, boolean realValue, SyncAPIConnector connector) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        Instrument instrumentData =  xtbDataSupplierService.getInstrumentData(periodCode, symbol, candlesNum, realValue, connector);
        if(Objects.nonNull(instrumentData)) {
            List<OHLC> candles = instrumentData.getCandles();
            return elliotPhaseWave.getElliotWavePhaseAndTrend(candles);
        }
        return null;
    }

    public Map<String, SupportResistanceLevel> getSupportResistanceLevels(PERIOD_CODE periodCode, String symbol, long candlesNum, boolean realValue) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        Instrument instrumentData =  xtbDataSupplierService.getInstrumentData(periodCode, symbol, candlesNum, realValue);
        List<OHLC> candles = instrumentData.getCandles();

        SupportResistanceCalculator calculator = new SupportResistanceCalculator(candles, 200);

        Map<String, SupportResistanceLevel> srValues = new HashMap<>();
        List<SupportResistanceLevel> supportLevels = calculator.calculateSupportLevels();
        srValues.put("S3", supportLevels.get(0));
        srValues.put("S2", supportLevels.get(1));
        srValues.put("S1", supportLevels.get(2));
        List<SupportResistanceLevel> resistanceLevels = calculator.calculateResistanceLevels();
        srValues.put("R3", resistanceLevels.get(0));
        srValues.put("R2", resistanceLevels.get(1));
        srValues.put("R1", resistanceLevels.get(2));

        return srValues;
    }


}
