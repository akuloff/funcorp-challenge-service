package co.fun.code.funcorpchallengeservice.crawler.model;

public interface IMediaSourceStateStorage {
  MediaSourceState getStateForId(String id) throws Exception;
  void updateState(MediaSourceState state) throws Exception;
}
