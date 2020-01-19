package co.fun.code.funcorpchallengeservice.crawler.strategy;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.Arrays;
import java.util.List;

public class HeadersAggregationStrategy implements AggregationStrategy {
  private final List<String> headers;

  public HeadersAggregationStrategy(String... headers) {
    this.headers = Arrays.asList(headers);
  }

  @Override
  public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
    headers.forEach(header -> oldExchange.getIn().setHeader(header, newExchange.getIn().getHeader(header)));
    return oldExchange;
  }
}
