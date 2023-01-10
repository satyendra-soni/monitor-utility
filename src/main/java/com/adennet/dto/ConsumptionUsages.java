package com.adennet.dto;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class ConsumptionUsages {
    private String subscriberNumber;
    private String startDate;
    private String endDate;
    private String initialQuantity;
    private String availableQuantity;

}
