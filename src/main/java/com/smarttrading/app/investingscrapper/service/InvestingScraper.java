package com.smarttrading.app.investingscrapper.service;
import com.smarttrading.app.investingscrapper.dto.SupportResistanceZoneScrap;
import com.smarttrading.app.investingscrapper.dto.TechnicalAnalysisDTO;
import io.micrometer.common.util.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InvestingScraper {

    public TechnicalAnalysisDTO scrapeInvestingData(Document doc) {
        Element techElement = doc.select("div.newTechStudiesRight.instrumentTechTab#techStudiesInnerWrap").first();

        String summary = techElement.select("div.summary span.buy").text();
        if(StringUtils.isBlank(summary)){
            summary = techElement.select("div.summary span.sell").text();
        }

        if(StringUtils.isBlank(summary)){
            summary = "Neutral";
        }

        Element maElement = techElement.select("div.summaryTableLine:contains(Moving Averages)").first();
        String maLabel = maElement.select("span:first-child").text();
        String maValue = maElement.select("span.greenFont.bold").text();
        if(StringUtils.isBlank(maValue)){
            maValue = maElement.select("span.redFont.bold").text();
        }
        String maBuy = maElement.select("i#maBuy").text();
        String maSell = maElement.select("i#maSell").text();

        Element tiElement = techElement.select("div.summaryTableLine:contains(Technical Indicators)").first();
        String tiLabel = tiElement.select("span:first-child").text();
        String tiValue = tiElement.select("span.greenFont.bold").text();
        if(StringUtils.isBlank(tiValue)){
            tiValue = maElement.select("span.redFont.bold").text();
        }
        String tiBuy = tiElement.select("i#tiBuy").text();
        String tiSell = tiElement.select("i#tiSell").text();


        return TechnicalAnalysisDTO.builder()
                .summary(summary)
                .movingAverages(maValue)
                .technicalIndicators(tiValue)
                .build();
    }

    public SupportResistanceZoneScrap getSupportResistanceValues(Document doc) {
        Map<String, Map<String, String>> supportResistanceValues = new HashMap<>();
        Elements supportResistanceSection = doc.select("#curr_table");
        Elements rows = supportResistanceSection.select("tr");
        for (Element row : rows) {
            String rowName = row.select(".first.left.bold.noWrap").text().trim();
            Elements values = row.select("td:not(.first)");
            Map<String, String> rowValues = new HashMap<>();
            for (int i = 0; i < values.size(); i++) {
                String value = values.get(i).text().trim();
                rowValues.put("Value " + (i+1), value);
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
    }
}
