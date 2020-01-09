package co.fun.code.funcorpchallengeservice.model;

import java.util.List;

public interface IFeedListStorage {
  List<FeedRecord> getRecords(RecordSearchParams recordSearchParams);
}
