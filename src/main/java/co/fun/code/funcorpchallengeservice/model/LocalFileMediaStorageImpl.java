package co.fun.code.funcorpchallengeservice.model;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

@Component
public class LocalFileMediaStorageImpl implements IMediaStorage {
  private final String localPath;

  public LocalFileMediaStorageImpl(@Value("${local.storage.path}") String localPath) {
    this.localPath = localPath;
  }

  @Override
  public MediaContent getMediaContent(String mediaLink) throws Exception {
    String fullPath = localPath + mediaLink;
    File file = new File(fullPath);
    return MediaContent.builder()
      .inputStream(new FileInputStream(fullPath))
      .size(file.length())
      .type(getMediaType(file.getName()))
      .build();
  }

  private MediaType getMediaType(String fname) {
    MediaType type = MediaType.IMAGE_STATIC;
    if (fname.endsWith(".gif")) {
      type = MediaType.IMAGE_GIF;
    } else if (fname.endsWith(".mp4")) {
      type = MediaType.VIDEO;
    }
    return type;
  }

  @Override
  public void storeMediaContent(String mediaLink, InputStream is) throws Exception {
    String fullPath = localPath + mediaLink;
    FileOutputStream os = new FileOutputStream(fullPath);
    IOUtils.copy(is, os);
    is.close();
    os.close();
  }

  @Override
  public void storeMediaContent(String mediaLink, String body) throws Exception {
    String fullPath = localPath + mediaLink;
    FileOutputStream os = new FileOutputStream(fullPath);
    os.write(body.getBytes());
    os.close();
  }
}
