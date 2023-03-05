//package com.smarttrading.app.datasupplier.dto;
//
//import yahoofinance.Stock;
//import yahoofinance.YahooFinance;
//import yahoofinance.histquotes.Interval;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.stream.Collectors;
//
//
//public class YahooFinanceMapper {
//
//    private Instrument getInstrumentBySymbol(String symbol, Interval interval) throws IOException {
//        Stock stock = YahooFinance.get(symbol, TimeFrame timeFrame);
//    }
//
//    private Instrument mapYahooFinanceToInstrument(Stock stock) throws IOException {
//        List<OHLC> ohlc = stock.getHistory().stream().map(candle -> OHLC.builder()
//                .open(candle.getOpen())
//                .close(candle.getClose())
//                .low(candle.getLow())
//                .high(candle.getHigh()).build()).toList();
//
//        Instrument.builder()
//                .value(stock.getQuote().getPrice())
//                .candles(ohlc)
//                .symbol(stock.getSymbol())
//                .timeFrame()
//                .build()
//    }
//}
