package co.fun.code.funcorpchallengeservice.crawler;

import co.fun.code.funcorpchallengeservice.crawler.model.CrawlerParams;
import co.fun.code.funcorpchallengeservice.crawler.model.ICrawlersInstanceLoader;
import co.fun.code.funcorpchallengeservice.model.IFeedRecordFilter;
import co.fun.code.funcorpchallengeservice.model.IFeedRecordsStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Order(1000)
@Slf4j
public class CrawlersMainRouteBuilder extends RouteBuilder {
  private final IFeedRecordsStorage feedRecordsStorage;
  private final IFeedRecordFilter filter;
  private final ICrawlersInstanceLoader crawlersInstanceLoader;

  @Autowired
  public CrawlersMainRouteBuilder(IFeedRecordsStorage feedRecordsStorage, IFeedRecordFilter filter, ICrawlersInstanceLoader crawlersInstanceLoader) {
    this.feedRecordsStorage = feedRecordsStorage;
    this.filter = filter;
    this.crawlersInstanceLoader = crawlersInstanceLoader;
  }

  @Override
  public void configure() throws Exception {
    List<CrawlerParams> crawlerParamsList = crawlersInstanceLoader.getCrawlersInstances();
    log.info("loaded crawler instances, count: {}", crawlerParamsList.size());

    from("timer?period={{timer.routine.msec}}")
      .log(LoggingLevel.INFO, "timer routine ... ");
  }
}
