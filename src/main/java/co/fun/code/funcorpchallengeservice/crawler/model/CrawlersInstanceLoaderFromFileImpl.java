package co.fun.code.funcorpchallengeservice.crawler.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class CrawlersInstanceLoaderFromFileImpl extends DefaultCrawlersInstanceLoader {
  private final String fileName;

  @Autowired
  public CrawlersInstanceLoaderFromFileImpl(ICrawlerInstanceStorer storer, String fileName) {
    super(storer);
    this.fileName = fileName;
  }

  @Override
  protected String getConfigBody() throws Exception {
    return new String(Files.readAllBytes(Paths.get(fileName)));
  }
}
