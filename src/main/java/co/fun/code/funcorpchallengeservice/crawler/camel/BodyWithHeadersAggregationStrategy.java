package co.fun.code.funcorpchallengeservice.crawler.camel;

import java.util.Arrays;

public class BodyWithHeadersAggregationStrategy extends AggregationStrategyComposition {
  public BodyWithHeadersAggregationStrategy(String... headers) {
    super(Arrays.asList(
      new BodyOnlyAggregationStrategy(),
      new HeadersAggregationStrategy(headers)
    ));
  }
}
