package co.fun.code.funcorpchallengeservice.crawler;

import co.fun.code.funcorpchallengeservice.crawler.camel.MergeAggregationStrategy;
import co.fun.code.funcorpchallengeservice.crawler.model.*;
import co.fun.code.funcorpchallengeservice.model.IFeedRecordFilter;
import co.fun.code.funcorpchallengeservice.model.IFeedRecordsStorage;
import co.fun.code.funcorpchallengeservice.model.IMediaStorage;
import co.fun.code.generatedservice.model.ExtendedFeedRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.direct.DirectConsumerNotAvailableException;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.List;

import static co.fun.code.funcorpchallengeservice.crawler.HeadersDefinition.*;

@Component
@Order(1000)
@Slf4j
public class CrawlersMainRouteBuilder extends RouteBuilder {
  protected final static String BROWSER_USER_AGENT = "Mozilla/5.0 Firefox/26.0";
  protected final static String SUCCESS_REQUEST = "success.request";

  private final IFeedRecordsStorage feedRecordsStorage;
  private final IFeedRecordFilter filter;
  private final ICrawlerInstanceLoaderFactory loaderFactory;
  private final IMediaSourceStateStorage stateStorage;
  private final ICrawlerInstanceStorer crawlerInstanceStorer;
  private final IMediaStorage mediaStorage;

  @Value("${crawlers.config.type}")
  private String crawlersConfigType;

  @Value("${crawlers.config.file}")
  private String crawlersConfigFile;

  @Value("${seda.concurrent.consumers}")
  private int sedaConcurrentConsumers;

  @Value("${seda.queue.size}")
  private int sedaQueueSize;

  @Value("${create.crawlers}")
  private Boolean createCrawlers;


  @Autowired
  public CrawlersMainRouteBuilder(IFeedRecordsStorage feedRecordsStorage,
                                  IFeedRecordFilter filter,
                                  IMediaSourceStateStorage stateStorage,
                                  ICrawlerInstanceLoaderFactory loaderFactory,
                                  ICrawlerInstanceStorer crawlerInstanceStorer,
                                  IMediaStorage mediaStorage) {
    this.feedRecordsStorage = feedRecordsStorage;
    this.filter = filter;
    this.stateStorage = stateStorage;
    this.loaderFactory = loaderFactory;
    this.crawlerInstanceStorer = crawlerInstanceStorer;
    this.mediaStorage = mediaStorage;
  }

  @Override
  public void configure() throws Exception {

    from("direct:perform-media-url-request")
      .removeHeaders("*", FEED_MESSAGE_URL)
      .setHeader("User-Agent", constant(BROWSER_USER_AGENT))
      .setExchangePattern(ExchangePattern.InOnly)
      .doTry()
        .toD(String.format("${headers.%s}", FEED_MESSAGE_URL))
        .setHeader(SUCCESS_REQUEST, constant(true))
      .doCatch(HttpOperationFailedException.class)
        .process(exchange -> {
          HttpOperationFailedException exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, HttpOperationFailedException.class);
          if (exception != null) {
              exchange.getIn().setHeader(SUCCESS_REQUEST, constant(false));
          }
        })
      .end();

    from(String.format("seda:records-to-store?size=%s&concurrentConsumers=%s", sedaQueueSize, sedaConcurrentConsumers))
      .log(LoggingLevel.INFO, String.format("save media for message, id: ${headers.%s}, url: ${headers.%s}", FEED_RECORD_ID, FEED_MESSAGE_URL))
      .setHeader(FEED_RECORD_BODY, body())
      .setBody(constant(null))
      .log(LoggingLevel.INFO, String.format("perform request to ${headers.%s}", FEED_MESSAGE_URL))
      .enrich("direct:perform-media-url-request", new MergeAggregationStrategy())
      .process(exchange -> {
        if (Boolean.TRUE.equals(exchange.getIn().getHeader(SUCCESS_REQUEST, Boolean.class))) {
          ExtendedFeedRecord record = exchange.getIn().getHeader(FEED_RECORD_BODY, ExtendedFeedRecord.class);
          mediaStorage.storeMediaContent(record.getMediaLinkId(), exchange.getIn().getBody(InputStream.class));
          record.setMediaStored(true);
          feedRecordsStorage.storeRecord(record);
        }
      });

    from("direct:send-records-list-to-storage")
      .split(body())
      .process(exchange -> {
        ExtendedFeedRecord feedRecord = exchange.getIn().getBody(ExtendedFeedRecord.class);
        exchange.getIn().setHeader(FEED_MESSAGE_URL, feedRecord.getExternalLink());
        exchange.getIn().setHeader(FEED_RECORD_ID, feedRecord.getId());
        boolean filtered = filter.filtered(feedRecord);
        if (filtered) {
          log.info(String.format("store record in storage, id: %s", feedRecord.getId()));
          feedRecordsStorage.storeRecord(feedRecord);
        }
        exchange.getIn().setHeader(FEED_RECORD_FILTERED, filtered);
      })
      .choice()
      .when(header(FEED_RECORD_FILTERED))
        .to(String.format("seda:records-to-store?size=%s&blockWhenFull=true", sedaQueueSize))
      .endChoice()
      .otherwise()
        .log(LoggingLevel.INFO, String.format("record not filtered, id: ${headers.%s}", FEED_RECORD_ID))
      .end();


    if (createCrawlers) {
      log.info("create crawlers routes ...");

      ICrawlersInstanceLoader loader = loaderFactory.getLoader(crawlersConfigType, crawlersConfigFile);
      loader.load();
      List<CrawlerParams> crawlerParamsList = crawlerInstanceStorer.getAllParams();
      log.info("loaded crawler instances, count: {}", crawlerParamsList.size());

      for (CrawlerParams params : crawlerParamsList) {
        log.info("create crawler route, params: {}", params);
        if (params.getRequestIntervalMsec() > 0 && !StringUtils.isEmpty(params.getSourceId())) {
          long delay = params.getStartDelayMsec() >= 0 ? params.getStartDelayMsec() : 0;
          from(String.format("timer?period=%s&repeatCount=1&delay=%s", params.getRequestIntervalMsec(), delay))
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
    } else {
      log.info("crawlers will not be created (tune disabled)");
    }

  }
}
