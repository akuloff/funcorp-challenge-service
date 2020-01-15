package co.fun.code.funcorpchallengeservice.model;

import co.fun.code.generatedservice.model.ExtendedFeedRecord;

public interface IFeedRecordFilter {
  boolean filtered(ExtendedFeedRecord record);
}
