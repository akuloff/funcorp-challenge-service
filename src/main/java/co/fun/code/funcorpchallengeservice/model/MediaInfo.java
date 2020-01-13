package co.fun.code.funcorpchallengeservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MediaInfo {
  private MediaType type;
  private long size;
}
