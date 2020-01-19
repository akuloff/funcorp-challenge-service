package co.fun.code.funcorpchallengeservice.crawler.giphy;

import co.fun.code.funcorpchallengeservice.crawler.HeadersDefinition;
import co.fun.code.funcorpchallengeservice.crawler.giphy.model.GiphyDataRecord;
import co.fun.code.funcorpchallengeservice.crawler.giphy.model.GiphySearchResponse;
import co.fun.code.funcorpchallengeservice.crawler.model.CrawlerParams;
import co.fun.code.funcorpchallengeservice.crawler.model.ICrawlerInstanceStorer;
import co.fun.code.funcorpchallengeservice.crawler.model.MediaSourceState;
import co.fun.code.funcorpchallengeservice.model.MediaType;
import co.fun.code.generatedservice.model.ExtendedFeedRecord;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static co.fun.code.funcorpchallengeservice.crawler.HeadersDefinition.FULL_REQUEST_URL;
import static co.fun.code.funcorpchallengeservice.crawler.giphy.Constants.API_URL;

@Component
public class GiphyRouteBuilder extends RouteBuilder {
  private static final String DO_PROCESS = "do.process";
  private final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  @Autowired
  private ICrawlerInstanceStorer crawlerInstanceStorer;

  private ExtendedFeedRecord feedRecordFromData(Exchange exchange, CrawlerParams crawlerParams, GiphyDataRecord dataRecord) throws Exception{
    String linkUrl = String.format("https://i.giphy.com/%s.gif", dataRecord.getId());
    ExtendedFeedRecord record = new ExtendedFeedRecord();
    record.setId(dataRecord.getId());
    record.setExternalId(dataRecord.getId());
    record.setTitle(dataRecord.getTitle());
    record.setLanguage(crawlerParams.getLanguage());
    record.setAllHeaders(mapper.writeValueAsString(exchange.getIn().getHeaders()));
    record.setSourceName(crawlerParams.getType());
    record.setMediaType(MediaType.IMAGE_GIF.name());
    record.setMediaStored(false);
    record.setTimestamp(System.currentTimeMillis());
    record.setExternalLink(new URI(linkUrl));
    return record;
  }

  @Override
  public void configure() throws Exception {

    from("direct:perform-request-giphy")
      .log(LoggingLevel.INFO, "performing giphy processing")
      .enrich("direct:get-media-source-params", (oldExchange, newExchange) -> {
        Message msg = oldExchange.getIn();
        String sourceId = msg.getHeader(HeadersDefinition.MEDIA_SOURCE_ID, String.class);
        boolean doProcess = false;
        if (StringUtils.isEmpty(sourceId)) {
          log.warn("{} not defined in headers", HeadersDefinition.MEDIA_SOURCE_ID);
        } else {
          CrawlerParams crawlerParams = crawlerInstanceStorer.getParams(sourceId);
          if (crawlerParams != null) {
            MediaSourceState mediaSourceState = msg.getHeader(HeadersDefinition.MEDIA_SOURCE_STATE, MediaSourceState.class);
            if (mediaSourceState != null) {
              long lastTime = mediaSourceState.getLastCallTimestamp();
              log.info("giphy processing, last time: {}, change call time ...", lastTime);
              mediaSourceState.setLastCallTimestamp(System.currentTimeMillis());
            } else {
              log.info("mediaSourceState not found in headers, create new state");
              mediaSourceState = MediaSourceState.builder()
                .id(sourceId)
                .lastCallTimestamp(System.currentTimeMillis())
                .build();
              msg.setHeader(HeadersDefinition.MEDIA_SOURCE_STATE, mediaSourceState);
            }
            if (crawlerParams.getApiConnection() != null) {
              String apiUrl = API_URL;
              if (!StringUtils.isEmpty(crawlerParams.getApiConnection().getApiUrl())) {
                apiUrl = crawlerParams.getApiConnection().getApiUrl();
              }
              String requestUrl = String.format("%s?api_key=%s&q=%s&lang=%s", apiUrl,
                crawlerParams.getApiConnection().getApiKey(), crawlerParams.getSearchQuery(), crawlerParams.getLanguage());
              msg.setHeader(FULL_REQUEST_URL, requestUrl);
              doProcess = true;
            }
          } else {
            log.warn("can not read crawler params from storer, sourceId: {}", sourceId);
          }
        }
        msg.setHeader(DO_PROCESS, doProcess);
        return oldExchange;
      })
      .choice()
        .when(header(DO_PROCESS))
          .log(LoggingLevel.INFO, "send request to Giphy ...")
          .toD(String.format("${headers.%s}", FULL_REQUEST_URL))
          .convertBodyTo(String.class)
          .process(exchange -> {
            CrawlerParams crawlerParams = crawlerInstanceStorer.getParams(exchange.getIn().getHeader(HeadersDefinition.MEDIA_SOURCE_ID, String.class));
            String bodyString = exchange.getIn().getBody(String.class);
            GiphySearchResponse response = mapper.readValue(bodyString, GiphySearchResponse.class);
            List<ExtendedFeedRecord> recordList = new ArrayList<>();
            for (GiphyDataRecord data : response.getData()) {
              recordList.add(feedRecordFromData(exchange, crawlerParams, data));
            }
            exchange.getIn().setBody(recordList);
          })
          .to("direct:send-records-list-to-storage")
      .endChoice()
      .otherwise()
        .log(LoggingLevel.INFO, "not processing request to Giphy")
      .end();


  }
}

