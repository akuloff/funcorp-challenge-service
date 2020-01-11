package co.fun.code.funcorpchallengeservice.crawler.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MediaSourceState {
  private String id;
  private String lastRecordId;
  private long lastRecordTimestamp;
  private long lastRequestTimestamp;
  private int lastPage;
  private int lastPageLimit;
}
