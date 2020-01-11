package co.fun.code.funcorpchallengeservice.crawler.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CrawlersInstanceLoaderFromFileImpl implements ICrawlersInstanceLoader {
  private final String fileName;

  public CrawlersInstanceLoaderFromFileImpl(String fileName) {
    this.fileName = fileName;
  }

  protected String getFileContents() throws Exception{
    return Arrays.toString(Files.readAllBytes(Paths.get(fileName)));
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<CrawlerParams> getCrawlersInstances() throws Exception {
    List<CrawlerParams> crawlerParams = new ArrayList<>();
    String allBody = getFileContents();
    if (!StringUtils.isEmpty(allBody)) {
      crawlerParams = new ObjectMapper().readValue(allBody, List.class);
    }
    return crawlerParams;
  }
}
