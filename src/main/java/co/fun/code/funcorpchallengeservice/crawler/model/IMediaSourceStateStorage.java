package co.fun.code.funcorpchallengeservice.crawler.model;

public interface IMediaSourceStateStorage {
  MediaSourceState getStateForId(String id);
  void updateState(MediaSourceState state) throws Exception;
}
