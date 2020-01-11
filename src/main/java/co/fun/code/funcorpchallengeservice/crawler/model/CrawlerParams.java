package co.fun.code.funcorpchallengeservice.crawler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CrawlerParams {
  private String type;
  private String indexName;
  @JsonProperty("interval")
  private long requestIntervalMsec;
}
