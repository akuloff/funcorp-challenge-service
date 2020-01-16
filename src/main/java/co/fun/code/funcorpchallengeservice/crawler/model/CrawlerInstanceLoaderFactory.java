package co.fun.code.funcorpchallengeservice.crawler.model;

import org.springframework.stereotype.Component;

@Component
public class CrawlerInstanceLoaderFactory implements ICrawlerInstanceLoaderFactory{
  @Override
  public ICrawlersInstanceLoader getLoader(String type, String link) throws Exception {
    switch (type) {
      case "file":
        return new CrawlersInstanceLoaderFromFileImpl(link);
    }
    throw new UnsupportedOperationException(String.format("crawler type %s not supported yet!", type));
  }
}
