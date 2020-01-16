package co.fun.code.funcorpchallengeservice.crawler.giphy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GiphyMeta {
  private int status;
  private String msg;
  @JsonProperty("response_id")
  private String responseId;
}
