package co.fun.code.funcorpchallengeservice.model;

public interface IFeedRecordsStorage {
  boolean isRecordExixts(String id);
  ExtendedFeedRecord getRecordById(String id);
  void storeRecord(ExtendedFeedRecord record) throws Exception;
}
