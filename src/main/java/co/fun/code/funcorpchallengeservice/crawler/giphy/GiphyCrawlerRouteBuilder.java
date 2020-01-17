package co.fun.code.funcorpchallengeservice.crawler.giphy;

import co.fun.code.funcorpchallengeservice.crawler.HeadersDefinition;
import co.fun.code.funcorpchallengeservice.crawler.model.MediaSourceState;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class GiphyCrawlerRouteBuilder extends RouteBuilder {
  private static final String NEED_DELAY = "need.delay";

  @Override
  public void configure() throws Exception {

    from("direct:perform-request-giphy")
      .log(LoggingLevel.INFO, "performing giphy processing")
      .setHeader(NEED_DELAY, constant(true))
      .enrich("direct:get-media-source-params", (oldExchange, newExchange) -> {
        Message msg = oldExchange.getIn();
        MediaSourceState mediaSourceState = msg.getHeader(HeadersDefinition.MEDIA_SOURCE_STATE, MediaSourceState.class);
        if (mediaSourceState != null) {
          long lastTime = mediaSourceState.getLastCallTimestamp();
          log.info("giphy processing, last time: {}, change call time ...", lastTime);
          mediaSourceState.setLastCallTimestamp(System.currentTimeMillis());
        } else {
          log.info("mediaSourceState not found in headers, create new state");
          mediaSourceState = MediaSourceState.builder()
            .id(msg.getHeader(HeadersDefinition.MEDIA_SOURCE_ID, String.class))
            .lastCallTimestamp(System.currentTimeMillis())
            .build();
          oldExchange.getIn().setHeader(NEED_DELAY, false);
          oldExchange.getIn().setHeader(HeadersDefinition.MEDIA_SOURCE_STATE, mediaSourceState);
        }
        return oldExchange;
      })
//      .choice()
//        .when(header(NEED_DELAY))
//          .delay(5000)
//        .endChoice()
//      .otherwise()
//        .log(LoggingLevel.INFO, "not need to delay")
      .end();

  }
}
