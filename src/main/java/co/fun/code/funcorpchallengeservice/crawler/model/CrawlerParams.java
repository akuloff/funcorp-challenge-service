package co.fun.code.funcorpchallengeservice.crawler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrawlerParams {
  private String type;
  private String sourceId;

  @JsonProperty("interval")
  private long requestIntervalMsec;
}
