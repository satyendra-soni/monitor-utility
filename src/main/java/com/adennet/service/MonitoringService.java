package com.adennet.service;

import com.adennet.dto.ConsumptionUsages;
import com.adennet.dto.DbResponse;
import com.adennet.dto.ServerDetail;
import com.adennet.dto.SessionCount;
import lombok.SneakyThrows;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.adennet.util.AppUtil.getDiskDetails;
import static com.adennet.util.AppUtil.parseDateTime;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@Slf4j
public class MonitoringService {
    private final MongoTemplate radiusDbMongoClient;
    private final MongoTemplate mediationDbMongoClient;
    private final JdbcTemplate usageMgmtDbClient;


    @Autowired
    public MonitoringService(@Qualifier("radiusDbMongoClient") MongoTemplate radiusDbMongoClient,
                             @Qualifier("mediationDbMongoClient") MongoTemplate mediationDbMongoClient,
                             @Qualifier("usageMgmtDbClient") JdbcTemplate usageMgmtDbClient) {
        this.radiusDbMongoClient = radiusDbMongoClient;
        this.mediationDbMongoClient = mediationDbMongoClient;
        this.usageMgmtDbClient = usageMgmtDbClient;
    }

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
        return radiusDbMongoClient.aggregate(agg, "radius_session", SessionCount.class).getUniqueMappedResult();
    }

    public void getConsumptionDataFromPostgres() {
        String sql = "SELECT subscriber_number,start_date,end_date,initial_quantity,available_quantity from consumption_usage_map WHERE status='ACTIVE'";
        List<ConsumptionUsages> usages = usageMgmtDbClient.query(
                sql,
                (rs, rowNum) ->
                        ConsumptionUsages.builder()
                                .subscriberNumber(rs.getString("subscriber_number"))
                                .startDate(rs.getString("start_date"))
                                .endDate(rs.getString("end_date"))
                                .initialQuantity(rs.getString("initial_quantity"))
                                .availableQuantity(rs.getString("available_quantity"))
                                .build()
        );
        System.out.println("usages = " + usages);
    }


    public DbResponse getSessionData(String subscriberNumber, String startDate, String endDate) {
        log.info("subscriberNumber ={},startDate={},endDate={} ", subscriberNumber, startDate, endDate);
        Query query = new Query();
        query.addCriteria(Criteria.where("subscriberNumber").is(subscriberNumber).and("createdAt").gt(parseDateTime(startDate)).lt(parseDateTime(endDate)));
        long count = radiusDbMongoClient.count(query, "radius_session");
        System.out.println("count== = " + count);
        Aggregation agg = newAggregation(
                match(Criteria.where("subscriberNumber").is(subscriberNumber).and("createdAt").gt(parseDateTime(startDate)).lt(parseDateTime(endDate))),
                group("$null").sum(AggregationExpression.from(MongoExpression.create("$toDouble:?0", "$cumulativeDataUsage"))).as("sum_cumulativeDataUsage"),
                project("sum_cumulativeDataUsage")

        );
        AggregationResults<DbResponse> radiusSession = radiusDbMongoClient.aggregate(agg, "radius_session", DbResponse.class);
        System.out.println("radiusSession.getUniqueMappedResult() = " + radiusSession.getUniqueMappedResult());
        return radiusSession.getUniqueMappedResult();
    }
}
