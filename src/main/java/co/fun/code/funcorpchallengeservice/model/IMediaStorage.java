package co.fun.code.funcorpchallengeservice.model;

import java.io.InputStream;

public interface IMediaStorage {
  MediaContent getMediaContent(String mediaLink) throws Exception;
  void storeMediaContent(String mediaLink, InputStream is) throws Exception;
  void storeMediaContent(String mediaLink, String body) throws Exception;
}
