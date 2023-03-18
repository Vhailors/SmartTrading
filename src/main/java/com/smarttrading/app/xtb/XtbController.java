package com.smarttrading.app.xtb;

import com.smarttrading.app.xtb.datasupplier.dto.Instrument;
import com.smarttrading.app.xtb.datasupplier.service.XtbDataSupplierService;
import com.smarttrading.app.xtb.dto.XtbRequest;
import com.smarttrading.app.xtb.service.XtbService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.response.APIErrorResponse;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
public class XtbController {

    private final XtbService xtbService;
    private final XtbDataSupplierService xtbDataSupplierService;

    @PostMapping("/open-trade/")
    public void execute(@RequestBody XtbRequest request) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        xtbService.executeTrade(request);
    }

    @GetMapping("/close-all/")
    public void closeAll() throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        xtbService.closeAllTrades();
    }

    @GetMapping("/balance/")
    public ResponseEntity<Double> getBalance() throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        return new ResponseEntity<>(xtbService.getBalance(), HttpStatus.OK);
    }

    @GetMapping("/canldes/")
    public ResponseEntity<Instrument> getCandles(@RequestParam PERIOD_CODE timeFrame, @RequestParam String symbol, @RequestParam long candles, @RequestParam(defaultValue = "false", required = false) boolean realValues) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        return new ResponseEntity<>(xtbDataSupplierService.getInstrumentData(timeFrame, symbol, candles, realValues), HttpStatus.OK);
    }

    @GetMapping("/swap/")
    public void swap(@RequestParam String symbol) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        xtbService.swapPositions(symbol);
    }

    @GetMapping("/ongoing/")
    public List<TradeRecord> getOngoingTrades() throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        return new ResponseEntity<>(xtbService.getOngoingTrades(), HttpStatus.OK).getBody();
    }
}
