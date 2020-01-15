package co.fun.code.funcorpchallengeservice.crawler;

import co.fun.code.funcorpchallengeservice.crawler.model.CrawlerParams;
import co.fun.code.funcorpchallengeservice.crawler.model.ICrawlersInstanceLoader;
import co.fun.code.funcorpchallengeservice.crawler.model.IMediaSourceStateStorage;
import co.fun.code.funcorpchallengeservice.model.IFeedRecordFilter;
import co.fun.code.funcorpchallengeservice.model.IFeedRecordsStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
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
  private final ICrawlersInstanceLoader crawlersInstanceLoader;
  private final IMediaSourceStateStorage stateStorage;

  @Autowired
  public CrawlersMainRouteBuilder(IFeedRecordsStorage feedRecordsStorage,
                                  IFeedRecordFilter filter,
                                  ICrawlersInstanceLoader crawlersInstanceLoader,
                                  IMediaSourceStateStorage stateStorage) {
    this.feedRecordsStorage = feedRecordsStorage;
    this.filter = filter;
    this.crawlersInstanceLoader = crawlersInstanceLoader;
    this.stateStorage = stateStorage;
  }

  @Override
  public void configure() throws Exception {
    List<CrawlerParams> crawlerParamsList = crawlersInstanceLoader.getCrawlersInstances();
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
