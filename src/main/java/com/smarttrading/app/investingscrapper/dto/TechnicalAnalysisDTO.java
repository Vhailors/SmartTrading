package com.smarttrading.app.investingscrapper.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TechnicalAnalysisDTO {
    String summary;
    String movingAverages;
    String technicalIndicators;
}
