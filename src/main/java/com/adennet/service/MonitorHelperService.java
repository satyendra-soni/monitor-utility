package com.adennet.service;


import com.adennet.dto.ConsumptionUsages;
import com.adennet.dto.CumulativeDataUsage;
import com.adennet.dto.QuantitySum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.adennet.util.AppUtil.parseDate;
import static com.adennet.util.AppUtil.parseDateTime;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@Slf4j
public class MonitorHelperService {
    @Getter
    private final MongoTemplate radiusDbMongoClient;
    @Getter
    private final MongoTemplate mediationDbMongoClient;
    private final JdbcTemplate usageMgmtDbClient;


    @Autowired
    public MonitorHelperService(@Qualifier("radiusDbMongoClient") MongoTemplate radiusDbMongoClient,
                                @Qualifier("mediationDbMongoClient") MongoTemplate mediationDbMongoClient,
                                @Qualifier("usageMgmtDbClient") JdbcTemplate usageMgmtDbClient) {
        this.radiusDbMongoClient = radiusDbMongoClient;
        this.mediationDbMongoClient = mediationDbMongoClient;
        this.usageMgmtDbClient = usageMgmtDbClient;
    }


    public List<ConsumptionUsages> getConsumptionDataFromPostgres() {
        System.out.println("getConsumptionDataFromPostgres Name = " + Thread.currentThread().getName());
        String sql = "SELECT id,subscriber_number,start_date,end_date,initial_quantity,available_quantity from consumption_usage_map WHERE status='ACTIVE'";

        return usageMgmtDbClient.query(
                sql,
                (rs, rowNum) -> ConsumptionUsages.builder()
                        .id(rs.getString("id"))
                        .subscriberNumber(rs.getString("subscriber_number"))
                        .startDate(rs.getString("start_date"))
                        .endDate(rs.getString("end_date"))
                        .initialQuantity(rs.getDouble("initial_quantity"))
                        .availableQuantity(rs.getDouble("available_quantity"))
                        .build()
        );
    }

    @Async
    public CompletableFuture<CumulativeDataUsage> getSessionData(String subscriberNumber, String startDate, String endDate) {
        System.out.println("MonitorHelperService.getSessionData"+Thread.currentThread().getName());
        log.info("subscriberNumber ={},startDate={},endDate={} ", subscriberNumber, startDate, endDate);

        Aggregation agg = newAggregation(
                match(Criteria.where("subscriberNumber").is(subscriberNumber).and("createdAt").gt(parseDate(startDate)).lt(parseDateTime(endDate))),
                group("$null").sum(AggregationExpression.from(MongoExpression.create("$toDouble:?0", "$cumulativeDataUsage"))).as("sumOfCumulativeDataUsage"),
                project("sumOfCumulativeDataUsage")

        );
        AggregationResults<CumulativeDataUsage> radiusSession = radiusDbMongoClient.aggregate(agg, "radius_session", CumulativeDataUsage.class);
        return CompletableFuture.completedFuture(radiusSession.getUniqueMappedResult());
    }



    @Async
    public CompletableFuture<QuantitySum> getBhmrData(String processId) {
        System.out.println("MonitorHelperService.getBhmrData"+Thread.currentThread().getName());
        Aggregation agg = newAggregation(
                match(Criteria.where("processId").is(processId)),
                group("$null").sum(AggregationExpression.from(MongoExpression.create("$toDouble:?0", "$quantity"))).as("sumOfQuantity"),
                project("sumOfQuantity")
        );
        AggregationResults<QuantitySum> billingHubMediationRecord = mediationDbMongoClient.aggregate(agg, "billing_hub_mediation_record", QuantitySum.class);
        System.out.println("billingHubMediationRecord.getUniqueMappedResult() = " + billingHubMediationRecord.getUniqueMappedResult());
        return CompletableFuture.completedFuture(billingHubMediationRecord.getUniqueMappedResult());
    }

    public long countData(String subscriberNumber, String startDate, String endDate) {
        Query query = new Query();
        query.addCriteria(Criteria.where("subscriberNumber").is(subscriberNumber).and("createdAt").gt(parseDateTime(startDate)).lt(parseDateTime(endDate)));
        long count = radiusDbMongoClient.count(query, "radius_session");
        System.out.println("count== = " + count);
        return count;
    }

}
