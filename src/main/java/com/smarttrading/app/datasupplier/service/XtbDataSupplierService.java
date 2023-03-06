package com.smarttrading.app.datasupplier.service;

import com.smarttrading.app.datasupplier.dto.Instrument;
import com.smarttrading.app.datasupplier.dto.OHLC;
import com.smarttrading.app.datasupplier.utils.DataSupplierUtils;
import com.smarttrading.app.xtb.serivce.XtbService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.RateInfoRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.ChartResponse;
import pro.xstore.api.sync.SyncAPIConnector;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Service

public class XtbDataSupplierService {

    private final XtbService xtbService;

    public Instrument getInstrumentData(PERIOD_CODE periodCode, String symbol, long candles) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        SyncAPIConnector connector = xtbService.init();
        ChartResponse chartResponse = APICommandFactory.executeChartLastCommand(connector, symbol, periodCode, DataSupplierUtils.getTimePeriodForReceiveCandles(periodCode, candles));
        double price = xtbService.getSymbolActualPrice(symbol);
        return mapXtbResponseToInstrument(chartResponse, periodCode, symbol, price);
    }

    private Instrument mapXtbResponseToInstrument(ChartResponse chartResponse, PERIOD_CODE periodCode, String symbol, double value){
        List<RateInfoRecord> records = chartResponse.getRateInfos();
        List<OHLC> ohlc = records.stream().map(x -> OHLC.builder()
                .open(x.getOpen())
                .high(x.getHigh())
                .low(x.getLow())
                .close(x.getClose()).build()).toList();
        return Instrument.builder()
                .timeFrame(periodCode)
                .price(value)
                .candles(ohlc)
                .symbol(symbol)
                .build();
    }
}
