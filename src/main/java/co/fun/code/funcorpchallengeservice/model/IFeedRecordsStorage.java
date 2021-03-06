package co.fun.code.funcorpchallengeservice.model;

import co.fun.code.generatedservice.model.ExtendedFeedRecord;
import co.fun.code.generatedservice.model.FeedRecord;

import java.util.List;

public interface IFeedRecordsStorage {
  boolean isRecordExists(String id);
  ExtendedFeedRecord getRecordById(String id) throws Exception;
  void storeRecord(ExtendedFeedRecord record) throws Exception;
  List<FeedRecord> getRecords(RecordSearchParams searchParams) throws Exception;
}
