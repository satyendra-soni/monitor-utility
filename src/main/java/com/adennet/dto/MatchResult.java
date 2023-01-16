package com.adennet.dto;

import lombok.*;

@Data
@Builder
public class MatchResult {
    private String subscriberNumber;
    private double usedQuantityInConsumptionUsagesMap;
    private double usedQuantityInRadiusSession;
    private double usedQuantityInMediationRecord;
}
