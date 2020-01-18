package co.fun.code.funcorpchallengeservice.crawler.model;

import java.util.List;

public interface ICrawlerInstanceStorer {
  void addParams(CrawlerParams params) throws Exception;
  CrawlerParams getParams(String sourceId);
  boolean isContains(String sourceId);
  List<CrawlerParams> getAllParams();
}
