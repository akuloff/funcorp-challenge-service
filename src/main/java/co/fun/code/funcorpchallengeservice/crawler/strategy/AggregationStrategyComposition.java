package co.fun.code.funcorpchallengeservice.crawler.strategy;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.List;

public class AggregationStrategyComposition implements AggregationStrategy {
  private final List<AggregationStrategy> strategies;

  public AggregationStrategyComposition(List<AggregationStrategy> strategies) {
    this.strategies = strategies;
  }

  @Override
  public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
    Exchange aggregate = oldExchange;
    for (AggregationStrategy strategy : strategies) {
      aggregate = strategy.aggregate(aggregate, newExchange);
    }
    return aggregate;
  }
}
