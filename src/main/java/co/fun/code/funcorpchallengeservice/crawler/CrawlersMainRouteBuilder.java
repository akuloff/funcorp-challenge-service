package co.fun.code.funcorpchallengeservice.crawler;

import co.fun.code.funcorpchallengeservice.crawler.model.*;
import co.fun.code.funcorpchallengeservice.model.IFeedRecordFilter;
import co.fun.code.funcorpchallengeservice.model.IFeedRecordsStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.direct.DirectConsumerNotAvailableException;
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
  private final ICrawlerInstanceStorer crawlerInstanceStorer;

  @Value("${crawlers.config.type}")
  private String crawlersConfigType;

  @Value("${crawlers.config.file}")
  private String crawlersConfigFile;

  @Value("${seda.concurrent.consumers}")
  private int sedaConcurrentConsumers;

  @Value("${seda.queue.size}")
  private int sedaQueueSize;


  @Autowired
  public CrawlersMainRouteBuilder(IFeedRecordsStorage feedRecordsStorage,
                                  IFeedRecordFilter filter,
                                  IMediaSourceStateStorage stateStorage,
                                  ICrawlerInstanceLoaderFactory loaderFactory,
                                  ICrawlerInstanceStorer crawlerInstanceStorer) {
    this.feedRecordsStorage = feedRecordsStorage;
    this.filter = filter;
    this.stateStorage = stateStorage;
    this.loaderFactory = loaderFactory;
    this.crawlerInstanceStorer = crawlerInstanceStorer;
  }

  @Override
  public void configure() throws Exception {
    ICrawlersInstanceLoader loader = loaderFactory.getLoader(crawlersConfigType, crawlersConfigFile);
    loader.load();
    List<CrawlerParams> crawlerParamsList = crawlerInstanceStorer.getAllParams();
    log.info("loaded crawler instances, count: {}", crawlerParamsList.size());

    from(String.format("seda:records-to-store?size=%s&concurrentConsumers=%s", sedaQueueSize, sedaConcurrentConsumers))
      .log(LoggingLevel.INFO, "processing message: ${body}")
      .delay(2000);

    from("direct:send-records-list-to-storage")
      .split(body())
      .to(String.format("seda:records-to-store?size=%s&blockWhenFull=true", sedaQueueSize));

    for (CrawlerParams params : crawlerParamsList) {
      log.info("create crawler route, params: {}", params);
      if (params.getRequestIntervalMsec() > 0 && !StringUtils.isEmpty(params.getSourceId())) {
        from(String.format("timer?period=%s", params.getRequestIntervalMsec()))
          .log(LoggingLevel.INFO, String.format("start timer routine for crawler id: %s", params.getSourceId()))
          .setHeader(HeadersDefinition.MEDIA_SOURCE_ID, constant(params.getSourceId()))
          .process(exchange -> {
            log.info("pre processing, headers: {}", exchange.getIn().getHeaders());
          })
          .doTry()
            .to("direct:get-media-source-params")
            .toD(String.format("direct:perform-request-%s", params.getType()), false)
            .to("direct:save-media-source-params")
          .doCatch(DirectConsumerNotAvailableException.class)
            .log(LoggingLevel.INFO, String.format("not found processing route for crawler type: %s, id: %s", params.getType(), params.getSourceId()))
          .end();
      }
    }

  }
}
