package com.smarttrading.app.ta;

import com.smarttrading.app.ta.dto.ElliotWaveResponse;
import com.smarttrading.app.ta.dto.SupportResistanceLevel;
import com.smarttrading.app.ta.dto.CandleStickPatternResponse;
import com.smarttrading.app.ta.service.TechnicalAnalysisService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;


@RestController
@AllArgsConstructor
@RequestMapping("/ta")
public class TechnicalAnalysisController {
    private final TechnicalAnalysisService technicalAnalysisService;

    @GetMapping("/sr/")
    public Map<String, SupportResistanceLevel> getSrValues(@RequestParam PERIOD_CODE timeFrame, @RequestParam String symbol, @RequestParam long candles, @RequestParam(defaultValue = "false", required = false) boolean realValues) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        return new ResponseEntity<>(technicalAnalysisService.getSupportResistanceLevels(timeFrame, symbol, candles, realValues), HttpStatus.OK).getBody();
    }

    @GetMapping("/ew/")
    public ElliotWaveResponse getElliotWave(@RequestParam PERIOD_CODE timeFrame, @RequestParam String symbol, @RequestParam long candles, @RequestParam(defaultValue = "false", required = false) boolean realValues) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        return new ResponseEntity<>(technicalAnalysisService.getElliotWave(timeFrame, symbol, candles, realValues, null), HttpStatus.OK).getBody();
    }

    @GetMapping("/candlestick-patterns/")
    public ResponseEntity<CandleStickPatternResponse> detectCandlestickPatterns(@RequestParam PERIOD_CODE timeFrame, @RequestParam String symbol, @RequestParam long candles, @RequestParam(defaultValue = "false", required = false) boolean realValues) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        CandleStickPatternResponse candleStickPatternResponse = technicalAnalysisService.detectCandleStickPattern(timeFrame, symbol, candles, realValues, null);
        if (Objects.nonNull(candleStickPatternResponse))
            return ResponseEntity.ok(candleStickPatternResponse);
        else return ResponseEntity.noContent().build();
    }
}
