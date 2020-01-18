package co.fun.code.funcorpchallengeservice.crawler.giphy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GiphyImages {
  @JsonProperty("original")
  private GiphyImage original;

  @JsonProperty("downsized_medium")
  private GiphyImage downsizedMedium;
}
