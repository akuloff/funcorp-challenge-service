package co.fun.code.funcorpchallengeservice.crawler;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelMainRouteBuilder extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    from("timer?period={{timer.routine.msec}}")
      .log(LoggingLevel.INFO, "timer routine ... ");
  }
}
