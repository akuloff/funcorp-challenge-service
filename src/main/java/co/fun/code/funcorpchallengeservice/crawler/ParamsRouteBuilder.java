package co.fun.code.funcorpchallengeservice.crawler;

import co.fun.code.funcorpchallengeservice.crawler.model.IMediaSourceStateStorage;
import co.fun.code.funcorpchallengeservice.crawler.model.MediaSourceState;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ParamsRouteBuilder extends RouteBuilder {

  @Autowired
  private IMediaSourceStateStorage mediaSourceStateStorage;

  @Override
  public void configure() throws Exception {

    from("direct:get-media-source-params")
      .process(exchange -> {
        String mediaSourceId = exchange.getIn().getHeader(HeadersDefinition.MEDIA_SOURCE_ID, String.class);
        if (!StringUtils.isEmpty(mediaSourceId)) {
          MediaSourceState mediaSourceState = mediaSourceStateStorage.getStateForId(mediaSourceId);
          if (mediaSourceState != null) {
            exchange.getIn().setHeader(HeadersDefinition.MEDIA_SOURCE_STATE, mediaSourceState);
          }
        }
      });

    from("direct:save-media-source-params")
      .process(exchange -> {
        MediaSourceState mediaSourceState = exchange.getIn().getHeader(HeadersDefinition.MEDIA_SOURCE_STATE, MediaSourceState.class);
        if (mediaSourceState != null) {
          mediaSourceStateStorage.updateState(mediaSourceState);
        } else {
          log.warn("mediaSourceState not defined, headers: {}", exchange.getIn().getHeaders());
        }
      });

  }
}
