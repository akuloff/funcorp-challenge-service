package co.fun.code.funcorpchallengeservice.crawler.model;

public interface ICrawlerInstanceLoaderFactory {
  ICrawlersInstanceLoader getLoader(String type, String link) throws Exception;
}
