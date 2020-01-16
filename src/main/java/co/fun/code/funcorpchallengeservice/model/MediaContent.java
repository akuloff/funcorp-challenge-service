package co.fun.code.funcorpchallengeservice.model;

import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;

@Getter
@Builder
public class MediaContent {
  private InputStream inputStream;
  private long size;
  private MediaType type;
}
