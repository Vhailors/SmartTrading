package com.smarttrading.app.ta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ElliotWaveResponse {
    ElliotWavePhase phase;
    Trend trend;
}
