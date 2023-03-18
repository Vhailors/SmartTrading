package com.smarttrading.app.scrapper.service;

import com.smarttrading.app.database.entity.SymbolMapping;
import com.smarttrading.app.database.service.SymbolMappingService;
import com.smarttrading.app.scrapper.dto.SupportResistanceZoneScrap;
import com.smarttrading.app.scrapper.dto.TechnicalAnalysisDTO;
import com.smarttrading.app.xtb.datasupplier.utils.DataSupplierUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.xml.ValidationException;
import org.springframework.stereotype.Service;
import pro.xstore.api.message.codes.PERIOD_CODE;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class InvestingScrapper {
    private final SymbolMappingService symbolMappingService;

    public TechnicalAnalysisDTO scrapeInvestingData(String symbol, PERIOD_CODE periodCode) throws IOException, NoSuchFieldException {
        try {
            String url = mapSymbolToUrl(symbol);
            Document doc = Jsoup.connect(url)
                    .data("period", Long.toString(DataSupplierUtils.periodAsLong(periodCode)))
                    .get();
//        Document doc = Jsoup.connect(url).get();
            Element techElement = doc.select("div.newTechStudiesRight.instrumentTechTab#techStudiesInnerWrap").first();

            String summary = techElement.select("div.summary span.buy").text();
            if (StringUtils.isBlank(summary)) {
                summary = techElement.select("div.summary span.sell").text();
            }

            if (StringUtils.isBlank(summary)) {
                summary = "Neutral";
            }

            Element maElement = techElement.select("div.summaryTableLine:contains(Moving Averages)").first();
            String maLabel = maElement.select("span:first-child").text();
            String maValue = maElement.select("span.greenFont.bold").text();
            if (StringUtils.isBlank(maValue)) {
                maValue = maElement.select("span.redFont.bold").text();
            }
            String maBuy = maElement.select("i#maBuy").text();
            String maSell = maElement.select("i#maSell").text();

            Element tiElement = techElement.select("div.summaryTableLine:contains(Technical Indicators)").first();
            String tiLabel = tiElement.select("span:first-child").text();
            String tiValue = tiElement.select("span.greenFont.bold").text();
            if (StringUtils.isBlank(tiValue)) {
                tiValue = maElement.select("span.redFont.bold").text();
            }
            String tiBuy = tiElement.select("i#tiBuy").text();
            String tiSell = tiElement.select("i#tiSell").text();


            return TechnicalAnalysisDTO.builder()
                    .summary(summary)
                    .movingAverages(maValue)
                    .technicalIndicators(tiValue)
                    .build();
        } catch (SocketTimeoutException e){
            return TechnicalAnalysisDTO.builder()
                    .summary("mock")
                    .movingAverages("mock")
                    .technicalIndicators("mock")
                    .build();
        }
    }

    public SupportResistanceZoneScrap getSupportResistanceValues(String symbol, PERIOD_CODE periodCode) throws IOException, NoSuchFieldException {
        try {
            String url = mapSymbolToUrl(symbol);

            Document doc = Jsoup.connect(url)
                    .data("period", Long.toString(DataSupplierUtils.periodAsLong(periodCode)))
                    .get();
            Map<String, Map<String, String>> supportResistanceValues = new HashMap<>();
            Elements supportResistanceSection = doc.select("#curr_table");
            Elements rows = supportResistanceSection.select("tr");
            for (Element row : rows) {
                String rowName = row.select(".first.left.bold.noWrap").text().trim();
                Elements values = row.select("td:not(.first)");
                Map<String, String> rowValues = new HashMap<>();
                for (int i = 0; i < values.size(); i++) {
                    String value = values.get(i).text().trim();
                    rowValues.put("Value " + (i + 1), value);
                }
                supportResistanceValues.put(rowName, rowValues);
            }
            return SupportResistanceZoneScrap.builder()
                    .S3(supportResistanceValues.get("Classic").get("Value 1"))
                    .S2(supportResistanceValues.get("Classic").get("Value 2"))
                    .S1(supportResistanceValues.get("Classic").get("Value 3"))
                    .R3(supportResistanceValues.get("Classic").get("Value 7"))
                    .R2(supportResistanceValues.get("Classic").get("Value 6"))
                    .R1(supportResistanceValues.get("Classic").get("Value 5"))
                    .build();
        } catch (SocketTimeoutException e){
                return SupportResistanceZoneScrap.builder()
                        .S3("mock")
                        .S2("mock")
                        .S1("mock")
                        .R3("mock")
                        .R2("mock")
                        .R1("mock")
                        .build();
            }
    }

    public String mapSymbolToUrl(String symbol) {
        Optional<SymbolMapping> symbolMapping = symbolMappingService.getAllSymbolMappings().stream().filter(x -> StringUtils.equals(x.getSymbol(), symbol)).findFirst();
        if (symbolMapping.isPresent()) {
            return symbolMapping.map(SymbolMapping::getInvestingUrl).get();
        } else {
            log.info("Cannot find url for symbol: {}", symbol);
        }
        return null;
    }

}
