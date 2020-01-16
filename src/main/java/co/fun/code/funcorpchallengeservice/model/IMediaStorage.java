package co.fun.code.funcorpchallengeservice.model;

public interface IMediaStorage {
  MediaContent getMediaContent(String mediaLink) throws Exception;
}
