package com.smarttrading.app.investingscrapper.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScrapResponse {
    TechnicalAnalysisDTO technicalAnalysisDTO;
    SupportResistanceZoneScrap supportResistanceZoneScrap;
}
