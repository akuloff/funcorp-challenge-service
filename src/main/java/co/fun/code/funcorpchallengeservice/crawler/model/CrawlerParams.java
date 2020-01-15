package co.fun.code.funcorpchallengeservice.crawler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CrawlerParams {
  private String type;
  @JsonProperty("source-id")
  private String sourceId;

  @JsonProperty("interval")
  private long requestIntervalMsec;

  @JsonProperty("query")
  private String searchQuery;

  @JsonProperty("max-records")
  private long maxHistoryRecords;

  @JsonProperty("max-days")
  private long maxHistoryDays;

  private String channel;
  private String language;
  private List<String> tags;

  @JsonProperty("deep-scan")
  private boolean deepScan;
  @JsonProperty("deep-scan-days")
  private long deepScanDays;
  @JsonProperty("deep-scan-records")
  private long deepScanRecords;
  @JsonProperty("deep-scan-interval")
  private long deepScanInterval;
}
