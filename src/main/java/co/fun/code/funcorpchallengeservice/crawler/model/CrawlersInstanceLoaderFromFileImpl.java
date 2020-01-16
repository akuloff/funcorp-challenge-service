package co.fun.code.funcorpchallengeservice.crawler.model;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class CrawlersInstanceLoaderFromFileImpl extends AbstractCrawlersInstanceLoader {
  private final String fileName;

  public CrawlersInstanceLoaderFromFileImpl(String fileName) {
    this.fileName = fileName;
  }

  @Override
  protected String getConfigBody() throws Exception {
    return new String(Files.readAllBytes(Paths.get(fileName)));
  }
}
