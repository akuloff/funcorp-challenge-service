package co.fun.code.funcorpchallengeservice.crawler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaSourceState {
  private String id;
  private String lastRecordId;
  private long lastCallTimestamp;
  private long lastRecordTimestamp;
  private long lastRequestTimestamp;
  private long lastPage;
  private long lastPageLimit;
  private long deepScanLastId;
  private long deepScan;
  private long totalRecords;
}
