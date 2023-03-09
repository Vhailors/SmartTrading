package com.smarttrading.app.investingstrategy.strategy;


import com.smarttrading.app.investingscrapper.service.InvestingScraper;
import com.smarttrading.app.ta.service.TechnicalAnalysisService;
import com.smarttrading.app.xtb.datasupplier.service.XtbDataSupplierService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MixedStrategy {

    private final XtbDataSupplierService xtbDataSupplierService;
    private final InvestingScraper investingScraper;
    private final TechnicalAnalysisService technicalAnalysisService;

}
