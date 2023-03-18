package com.smarttrading.app.scrapper.dto;


import lombok.Builder;
import lombok.Data;

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
