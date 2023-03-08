package com.smarttrading.app.ai;

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

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class NeuralNetworkService {
    private final OHLCNeuralNetwork ohlcNeuralNetwork;
    private final XtbDataSupplierService xtbDataSupplierService;

    public double[] predictValues(PERIOD_CODE periodCode, String symbol, long candlesNum, boolean realValue) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        Instrument instrumentData =  xtbDataSupplierService.getInstrumentData(periodCode, symbol, candlesNum, realValue);
        List<OHLC> candles = instrumentData.getCandles();
        return ohlcNeuralNetwork.createModel(candles);
    }
}
