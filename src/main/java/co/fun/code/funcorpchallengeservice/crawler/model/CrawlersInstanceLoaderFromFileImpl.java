package co.fun.code.funcorpchallengeservice.crawler.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

  @Override
  public List<CrawlerParams> getCrawlersInstances() throws Exception {
    List<CrawlerParams> paramsList = new ArrayList<>();
    String allBody = getFileContents();
    if (!StringUtils.isEmpty(allBody)) {
      ObjectMapper objectMapper = new ObjectMapper();
      CrawlerParams[] paramsArray = objectMapper.readValue(allBody, CrawlerParams[].class);
      paramsList = Arrays.asList(paramsArray);
    }
    HashMap<String, String> sourceMap = new HashMap<>();
    for (CrawlerParams p: paramsList) {
      if (sourceMap.containsKey(p.getSourceId())) {
        throw new CrawlerException("sourceId must be unique in all crawlers!");
      } else {
        sourceMap.put(p.getSourceId(), p.getType());
      }
    }
    return paramsList;
  }
}
