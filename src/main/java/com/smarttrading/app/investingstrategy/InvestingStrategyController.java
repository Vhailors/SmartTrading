package com.smarttrading.app.investingstrategy;


import com.smarttrading.app.investingstrategy.dto.AllStrategyVariables;
import com.smarttrading.app.investingstrategy.riskmanagement.RiskManagementService;
import com.smarttrading.app.investingstrategy.strategy.BalancedStrategy;
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

@RestController
@RequestMapping("/strategy")
@AllArgsConstructor
public class InvestingStrategyController {

    private final BalancedStrategy mixedStrategy;
    private final RiskManagementService riskManagementService;

    @GetMapping("/get-all/")
    public ResponseEntity<AllStrategyVariables> getAllVariables(@RequestParam PERIOD_CODE timeFrame, @RequestParam String symbol, @RequestParam long candles, @RequestParam(defaultValue = "true", required = false) boolean realValues) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException, NoSuchFieldException {
        return new ResponseEntity<>(mixedStrategy.getAllStrategyVariables(timeFrame, symbol, candles, realValues, null), HttpStatus.OK);
    }

    @GetMapping("/update-ts/")
    public void udapteTs() throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException, NoSuchFieldException {
        riskManagementService.updateTS();
    }
}
