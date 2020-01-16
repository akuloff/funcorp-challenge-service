package co.fun.code.funcorpchallengeservice.crawler.giphy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


@Getter
public class GiphyImage {
  private String frames;
  private String hash;
  private String height;
  private String width;
  private String mp4;

  @JsonProperty("mp4_size")
  private String mp4Size;

  private String url;
  private String size;

  private String webp;

  @JsonProperty("webp_size")
  private String webpSize;
}
