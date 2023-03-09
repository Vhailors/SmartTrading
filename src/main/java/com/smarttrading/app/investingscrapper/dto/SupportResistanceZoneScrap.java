package com.smarttrading.app.investingscrapper.dto;


import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
public class SupportResistanceZoneScrap {
    String S1;
    String S2;
    String S3;
    String R1;
    String R2;
    String R3;
}
