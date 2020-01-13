package co.fun.code.funcorpchallengeservice;

import co.fun.code.funcorpchallengeservice.crawler.model.CrawlerParams;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class CrawlerSerializationTests {

  @Test
  public void testParamsSerializationWithAnnotationName() throws Exception{
    CrawlerParams params = CrawlerParams.builder()
      .requestIntervalMsec(3000)
      .sourceId("index_123")
      .type("type123")
      .build();
    ObjectMapper mapper = new ObjectMapper();
    String jsonValue = mapper.writeValueAsString(params);
    Assert.assertTrue(jsonValue.contains("\"interval\""));
  }
}
