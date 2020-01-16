package co.fun.code.funcorpchallengeservice.crawler.giphy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GiphyPagination {
  @JsonProperty("total_count")
  private long totalCount;
  private long count;
  private long offset;
}
