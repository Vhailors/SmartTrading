package com.smarttrading.app.investingscrapper;

import com.smarttrading.app.investingscrapper.dto.ScrapResponse;
import com.smarttrading.app.investingscrapper.dto.SupportResistanceZoneScrap;
import com.smarttrading.app.investingscrapper.dto.TechnicalAnalysisDTO;
import com.smarttrading.app.investingscrapper.service.InvestingScraper;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;

import java.io.IOException;
@RestController
@AllArgsConstructor
public class TechnicalAnalysisScrapperController {

    private final InvestingScraper technicalAnalysisScraper;

    @GetMapping("/scrap/")
    public ScrapResponse scrap(@RequestParam String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        TechnicalAnalysisDTO technicalAnalysisDTO = technicalAnalysisScraper.scrapeInvestingData(doc);
        SupportResistanceZoneScrap supportResistanceValues = technicalAnalysisScraper.getSupportResistanceValues(doc);
        ScrapResponse response = ScrapResponse.builder()
                .technicalAnalysisDTO(technicalAnalysisDTO)
                .supportResistanceZoneScrap(supportResistanceValues)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK).getBody();

    }

}
