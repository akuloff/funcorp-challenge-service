package co.fun.code.funcorpchallengeservice.crawler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CrawlerApiConnection {
  @JsonProperty("api-key")
  private String apiKey;

  @JsonProperty("client-id")
  private String clientId;

  @JsonProperty("user-name")
  private String userName;

  @JsonProperty("user-passwd")
  private String userPasswd;

  @JsonProperty("api-url")
  private String apiUrl;
}
