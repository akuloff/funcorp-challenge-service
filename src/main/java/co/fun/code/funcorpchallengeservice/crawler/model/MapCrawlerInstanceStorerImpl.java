package co.fun.code.funcorpchallengeservice.crawler.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MapCrawlerInstanceStorerImpl implements ICrawlerInstanceStorer {
  private Map<String, CrawlerParams> paramsMap = new ConcurrentHashMap<>();

  @Override
  public void addParams(CrawlerParams params) throws Exception {
    paramsMap.put(params.getSourceId(), params);
  }

  @Override
  public CrawlerParams getParams(String sourceId) {
    return paramsMap.get(sourceId);
  }

  @Override
  public boolean isContains(String sourceId) {
    return paramsMap.containsKey(sourceId);
  }

  @Override
  public List<CrawlerParams> getAllParams() {
    return new ArrayList<>(paramsMap.values());
  }
}
