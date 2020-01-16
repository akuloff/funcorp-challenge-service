package co.fun.code.funcorpchallengeservice.crawler;

import co.fun.code.funcorpchallengeservice.crawler.model.CrawlerParams;
import co.fun.code.funcorpchallengeservice.crawler.model.ICrawlerInstanceLoaderFactory;
import co.fun.code.funcorpchallengeservice.crawler.model.ICrawlersInstanceLoader;
import co.fun.code.funcorpchallengeservice.crawler.model.IMediaSourceStateStorage;
import co.fun.code.funcorpchallengeservice.model.IFeedRecordFilter;
import co.fun.code.funcorpchallengeservice.model.IFeedRecordsStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@Order(1000)
@Slf4j
public class CrawlersMainRouteBuilder extends RouteBuilder {
  private final IFeedRecordsStorage feedRecordsStorage;
  private final IFeedRecordFilter filter;
  private final ICrawlerInstanceLoaderFactory loaderFactory;
  private final IMediaSourceStateStorage stateStorage;

  @Value("${crawlers.config.type}")
  private String crawlersConfigType;

  @Value("${crawlers.config.file}")
  private String crawlersConfigFile;

  @Autowired
  public CrawlersMainRouteBuilder(IFeedRecordsStorage feedRecordsStorage,
                                  IFeedRecordFilter filter,
                                  IMediaSourceStateStorage stateStorage,
                                  ICrawlerInstanceLoaderFactory loaderFactory) {
    this.feedRecordsStorage = feedRecordsStorage;
    this.filter = filter;
    this.stateStorage = stateStorage;
    this.loaderFactory = loaderFactory;
  }

  @Override
  public void configure() throws Exception {
    ICrawlersInstanceLoader loader = loaderFactory.getLoader(crawlersConfigType, crawlersConfigFile);
    List<CrawlerParams> crawlerParamsList = loader.getCrawlersInstances();
    log.info("loaded crawler instances, count: {}", crawlerParamsList.size());

    for (CrawlerParams params : crawlerParamsList) {
      log.info("params: {}", params);
      if (params.getRequestIntervalMsec() > 0 && !StringUtils.isEmpty(params.getSourceId())) {
        from(String.format("timer?period=%s", params.getRequestIntervalMsec()))
          .log(LoggingLevel.INFO, String.format("timer routine for crawler id: %s", params.getSourceId()))
          .toD("processing-", true);
      }
    }

  }
}
