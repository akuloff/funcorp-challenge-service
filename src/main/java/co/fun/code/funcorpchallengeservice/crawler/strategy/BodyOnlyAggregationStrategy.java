package co.fun.code.funcorpchallengeservice.crawler.strategy;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class BodyOnlyAggregationStrategy implements AggregationStrategy {
  @Override
  public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
    if (oldExchange == null) return newExchange;
    oldExchange.getIn().setBody(newExchange.getIn().getBody());
    return oldExchange;
  }
}
