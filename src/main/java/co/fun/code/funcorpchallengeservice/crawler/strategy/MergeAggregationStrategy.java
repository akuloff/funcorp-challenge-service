package co.fun.code.funcorpchallengeservice.crawler.strategy;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class MergeAggregationStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        oldExchange.getIn().getHeaders().putAll(newExchange.getIn().getHeaders());
        if (newExchange.getIn().getBody() != null) {
            oldExchange.getIn().setBody(newExchange.getIn().getBody());
        }
        return oldExchange;
    }
}