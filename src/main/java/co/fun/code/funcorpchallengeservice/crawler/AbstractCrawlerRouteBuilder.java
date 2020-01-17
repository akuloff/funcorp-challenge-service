package co.fun.code.funcorpchallengeservice.crawler;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.springframework.util.StringUtils;

public abstract class AbstractCrawlerRouteBuilder extends RouteBuilder {

  protected String getPrefix() {
    return null;
  }

  protected String direct(String s) {
    String prefix = getPrefix();
    if (StringUtils.isEmpty(prefix)) {
      return String.format("direct:%s", s);
    } else {
      return String.format("direct:%s.%s", prefix, s);
    }
  }

  protected RouteDefinition internalDirect(String routeId) {
    return from(direct(routeId)).id(String.format("%s", routeId));
  }

  protected String httpDirect(String s) {
    return String.format("direct:http.%s", s);
  }

}
