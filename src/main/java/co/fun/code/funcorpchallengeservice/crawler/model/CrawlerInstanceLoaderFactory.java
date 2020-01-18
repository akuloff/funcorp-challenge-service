package co.fun.code.funcorpchallengeservice.crawler.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CrawlerInstanceLoaderFactory implements ICrawlerInstanceLoaderFactory{
  @Autowired
  ICrawlerInstanceStorer storer;

  @Override
  public ICrawlersInstanceLoader getLoader(String type, String link) throws Exception {
    switch (type) {
      case "file":
        return new CrawlersInstanceLoaderFromFileImpl(storer, link);
    }
    throw new UnsupportedOperationException(String.format("crawler type %s not supported yet!", type));
  }
}
