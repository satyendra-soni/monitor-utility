package com.adennet.service;

import com.adennet.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.adennet.util.AppUtil.getDiskDetails;
import static com.adennet.util.AppUtil.writeObjectToJsonFile;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MonitoringService {

    private final MonitorHelperService monitorHelperService;
    private final ObjectMapper mapper;

    @SneakyThrows
    public Map<String, String> diskDetails(String server, ServerDetail serverDetail) {
        Map<String, String> diskDetails = getDiskDetails(server, serverDetail);
        /*HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(diskDetails, headers);
        Payload payload= Payload.builder()
                .text("Hello From Code")
                .build();
        Slack slack = Slack.getInstance();
        String url="https://hooks.slack.com/services/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";
        WebhookResponse codeTest = slack.send(url, payload);*/
        return diskDetails;
    }

    public SessionCount activeSession(boolean isValid) {
        Aggregation agg = newAggregation(
                match(Criteria.where("isValid").is(isValid)),
                group("$null").count().as("count")
        );
        return monitorHelperService.getRadiusDbMongoClient().aggregate(agg, "radius_session", SessionCount.class).getUniqueMappedResult();
    }

    @Async()
    public void performDataMatching() {
        long start = System.currentTimeMillis();
        List<ConsumptionUsages> consumptionUsages = monitorHelperService.getConsumptionDataFromPostgres();

        List<MatchResult> matchResultList = consumptionUsages.stream()
                .map(usage -> {
                    CompletableFuture<CumulativeDataUsage> sessionData = monitorHelperService.getSessionData(usage.getSubscriberNumber(), usage.getStartDate(), usage.getEndDate());
                    CompletableFuture<QuantitySum> bhmrData = monitorHelperService.getBhmrData(usage.getId());
                    CompletableFuture.allOf(sessionData, bhmrData).join();
                    CumulativeDataUsage cumulativeDataUsage;
                    QuantitySum quantitySum;
                    try {
                        cumulativeDataUsage = sessionData.get();
                        quantitySum = bhmrData.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                    if (cumulativeDataUsage != null && quantitySum != null) {
                        double usedQuantityInConsumptionUsagesMap = usage.getInitialQuantity() - usage.getAvailableQuantity();
                        if (usedQuantityInConsumptionUsagesMap != cumulativeDataUsage.getSumOfCumulativeDataUsage() || usedQuantityInConsumptionUsagesMap != quantitySum.getSumOfQuantity()) {
                            return MatchResult.builder()
                                    .subscriberNumber(usage.getSubscriberNumber())
                                    .usedQuantityInConsumptionUsagesMap(usedQuantityInConsumptionUsagesMap)
                                    .usedQuantityInRadiusSession(cumulativeDataUsage.getSumOfCumulativeDataUsage())
                                    .usedQuantityInMediationRecord(quantitySum.getSumOfQuantity())
                                    .build();
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        writeObjectToJsonFile(mapper, matchResultList, System.getenv("HOME").concat("/monitor/" + LocalDateTime.now() + "monitor.json"));
        log.info("Time taken to complete performDataMatching task={}, at={}", System.currentTimeMillis() - start, LocalDateTime.now());
    }

}
