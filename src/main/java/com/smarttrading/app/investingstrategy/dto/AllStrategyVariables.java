package com.smarttrading.app.investingstrategy.dto;

import com.smarttrading.app.scrapper.dto.SupportResistanceZoneScrap;
import com.smarttrading.app.scrapper.dto.TechnicalAnalysisDTO;
import com.smarttrading.app.ta.dto.CandleStickPatternResponse;
import com.smarttrading.app.ta.dto.ElliotWaveResponse;
import com.smarttrading.app.ta.dto.SupportResistanceLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class AllStrategyVariables {
    TechnicalAnalysisDTO scrappedTa;
    SupportResistanceZoneScrap scrappedSrZones;
    CandleStickPatternResponse candleSticks;
    ElliotWaveResponse elliotWaves;
    Map<String, SupportResistanceLevel> calculatedSrZones;
}
