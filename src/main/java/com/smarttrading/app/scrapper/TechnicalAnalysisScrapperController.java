package com.smarttrading.app.scrapper;

import com.smarttrading.app.scrapper.dto.ScrapResponse;
import com.smarttrading.app.scrapper.dto.SupportResistanceZoneScrap;
import com.smarttrading.app.scrapper.dto.TechnicalAnalysisDTO;
import com.smarttrading.app.scrapper.service.InvestingScrapper;
import com.smarttrading.app.scrapper.service.TradingViewScrapperService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
@RestController
@AllArgsConstructor
public class TechnicalAnalysisScrapperController {

    private final InvestingScrapper technicalAnalysisScraper;
    private final TradingViewScrapperService tradingViewScrapperService;

    @GetMapping("/scrap/")
    public void scrap() throws IOException, NoSuchFieldException {
        tradingViewScrapperService.srap();
    }
}
