package com.smarttrading.app.ai;


import com.smarttrading.app.ai.service.StockPricePrediction;
import com.smarttrading.app.ta.dto.SupportResistanceLevel;
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

@RestController
@RequestMapping("/ai")
@AllArgsConstructor
public class NeuralNetworkController {

    private final NeuralNetworkService neuralNetworkService;

    private final StockPricePrediction stockPricePrediction;
    @GetMapping("/predict/")
    public double[] predictCloseValue(@RequestParam PERIOD_CODE timeFrame, @RequestParam String symbol, @RequestParam long candles, @RequestParam(defaultValue = "false", required = false) boolean realValues) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        return new ResponseEntity<>(neuralNetworkService.predictValues(timeFrame, symbol, candles, realValues), HttpStatus.OK).getBody();
    }

    @GetMapping("/st-predict/")
    public void stockPredictCloseValue(@RequestParam PERIOD_CODE timeFrame, @RequestParam String symbol, @RequestParam long candles, @RequestParam(defaultValue = "false", required = false) boolean realValues) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
       stockPricePrediction.predict(timeFrame, symbol, candles, realValues);
    }
}
