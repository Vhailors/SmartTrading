package com.smarttrading.app.xtb.datasupplier.service;

import com.smarttrading.app.ai.dto.OHLCV;
import com.smarttrading.app.xtb.datasupplier.dto.Instrument;
import com.smarttrading.app.xtb.datasupplier.dto.OHLC;
import com.smarttrading.app.xtb.datasupplier.utils.DataSupplierUtils;
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

    public Instrument getInstrumentData(PERIOD_CODE periodCode, String symbol, long candles, boolean realValues) throws APIErrorResponse, APICommunicationException, IOException, APIReplyParseException, APICommandConstructionException {
        SyncAPIConnector connector = xtbService.init();
        ChartResponse chartResponse = APICommandFactory.executeChartLastCommand(connector, symbol, periodCode, DataSupplierUtils.getTimePeriodForReceiveCandles(periodCode, candles));
        double price = xtbService.getSymbolActualPrice(symbol);
        return mapXtbResponseToInstrument(chartResponse, periodCode, symbol, price, realValues);
    }

    private Instrument mapXtbResponseToInstrument(ChartResponse chartResponse, PERIOD_CODE periodCode, String symbol, double value, boolean realValues) {

        int numberToMove;
        if (realValues) {
            numberToMove = DataSupplierUtils.countDecimalPlaces(value);
        } else {
            numberToMove = 0;
        }

        List<RateInfoRecord> records = chartResponse.getRateInfos();

        List<OHLC> ohlc = records.stream().map(x -> OHLC.builder()
                .open(DataSupplierUtils.moveDecimalPointLeft(x.getOpen(), numberToMove))
                .high(DataSupplierUtils.moveDecimalPointLeft(x.getOpen() + x.getHigh(), numberToMove))
                .low(DataSupplierUtils.moveDecimalPointLeft(x.getOpen() + x.getLow(), numberToMove))
                .close(DataSupplierUtils.moveDecimalPointLeft(x.getOpen() + x.getClose(), numberToMove))
                .build()).toList();

        List<OHLCV> OHLCV = records.stream().map(x -> com.smarttrading.app.ai.dto.OHLCV.builder()
                .open(DataSupplierUtils.moveDecimalPointLeft(x.getOpen(), numberToMove).doubleValue())
                .high(DataSupplierUtils.moveDecimalPointLeft(x.getOpen() + x.getHigh(), numberToMove).doubleValue())
                .low(DataSupplierUtils.moveDecimalPointLeft(x.getOpen() + x.getLow(), numberToMove).doubleValue())
                .close(DataSupplierUtils.moveDecimalPointLeft(x.getOpen() + x.getClose(), numberToMove).doubleValue())
                .volume(x.getVol())
                .build()).toList();


        return Instrument.builder()
                .timeFrame(periodCode)
                .price(value)
                .candles(ohlc)
                .candlesDouble(OHLCV)
                .symbol(symbol)
                .build();
    }
}
