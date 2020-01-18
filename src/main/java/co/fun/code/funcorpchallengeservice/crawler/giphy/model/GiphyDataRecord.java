package co.fun.code.funcorpchallengeservice.crawler.giphy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GiphyDataRecord {
  private String id;
  private String url;

  @JsonProperty("trending_datetime")
  private String trendingDatetime;

  @JsonProperty("import_datetime")
  private String importDatetime;

  private String title;
  private GiphyImages images;
}
