package co.fun.code.funcorpchallengeservice.crawler.model;

import java.util.List;

public interface ICrawlersInstanceLoader {
  List<CrawlerParams> getCrawlersInstances() throws Exception;
}
