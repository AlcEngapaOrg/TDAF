package es.tid.cloud.tdaf.accounting.filtering.aggregator;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class MapAggregator implements AggregationStrategy {
    @Override
    @SuppressWarnings("unchecked")
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Map<String, Object> newMap = newExchange.getIn().getBody(Map.class);
        Map<String, Object> oldMap = (oldExchange != null) ? oldExchange.getIn().getBody(Map.class) : null;
        if (newMap == null) {
            throw new IllegalArgumentException("This aggregation expect a map in the body of the new exchange.");
        }
        if (oldMap != null) {
            newMap.putAll(oldMap);
        }
        return newExchange;
    }
}
