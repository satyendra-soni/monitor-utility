package com.adennet.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class ConsumptionUsages {
    private String id;
    private String subscriberNumber;
    private String startDate;
    private String endDate;
    private double initialQuantity;
    private double availableQuantity;

}
