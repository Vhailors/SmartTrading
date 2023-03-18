package com.smarttrading.app.scrapper.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScrapResponse {
    TechnicalAnalysisDTO technicalAnalysisDTO;
    SupportResistanceZoneScrap supportResistanceZoneScrap;
}
