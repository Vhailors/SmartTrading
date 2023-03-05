package com.smarttrading.app.xtb.controller;

import com.smarttrading.app.xtb.dto.XtbRequest;
import com.smarttrading.app.xtb.serivce.XtbService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class XtbController {

    private final XtbService xtbService;

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
}
