package co.fun.code.funcorpchallengeservice.model;

public interface IFeedRecordsStorage {
  ExtendedFeedRecord getRecordById(String id);
  void storeRecord(ExtendedFeedRecord record) throws Exception;
}
