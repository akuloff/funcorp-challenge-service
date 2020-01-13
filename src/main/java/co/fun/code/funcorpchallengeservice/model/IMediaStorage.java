package co.fun.code.funcorpchallengeservice.model;

import java.io.InputStream;

public interface IMediaStorage {
  InputStream getMediaStream(String mediaLink);
  MediaInfo getMediaBodyInfo(String mediaLink);
}
