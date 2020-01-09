package co.fun.code.funcorpchallengeservice.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class FeedRecordInfo {
  private String id;
  private String hashData;
  private Instant time;
  private String contentInfo;
}
