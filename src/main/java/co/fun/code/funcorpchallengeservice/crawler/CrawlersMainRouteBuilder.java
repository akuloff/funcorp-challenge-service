package co.fun.code.funcorpchallengeservice.crawler;

import co.fun.code.funcorpchallengeservice.model.IFeedRecordsStorage;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CrawlersMainRouteBuilder extends RouteBuilder {

  private final IFeedRecordsStorage feedRecordsStorage;

  @Autowired
  public CrawlersMainRouteBuilder(IFeedRecordsStorage feedRecordsStorage) {
    this.feedRecordsStorage = feedRecordsStorage;
  }

  @Override
  public void configure() throws Exception {
    from("timer?period={{timer.routine.msec}}")
      .log(LoggingLevel.INFO, "timer routine ... ");
  }
}
