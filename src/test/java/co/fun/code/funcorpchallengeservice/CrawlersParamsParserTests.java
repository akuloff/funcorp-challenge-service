package co.fun.code.funcorpchallengeservice;

import co.fun.code.funcorpchallengeservice.crawler.model.CrawlerException;
import co.fun.code.funcorpchallengeservice.crawler.model.CrawlerParams;
import co.fun.code.funcorpchallengeservice.crawler.model.CrawlersInstanceLoaderFromFileImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class CrawlersParamsParserTests {

  @Test
  public void testParamsSerializationWithAnnotationName() throws Exception {
    CrawlerParams params = CrawlerParams.builder()
      .requestIntervalMsec(3000)
      .sourceId("index_123")
      .type("type123")
      .build();
    ObjectMapper mapper = new ObjectMapper();
    String jsonValue = mapper.writeValueAsString(params);
    Assert.assertTrue(jsonValue.contains("\"interval\""));
  }

  @Test
  public void testCrawlersInstanceLoaderFromFileImpl() throws Exception {
    List<CrawlerParams> crawlerParams = new ArrayList<>();
    crawlerParams.add(CrawlerParams.builder()
      .requestIntervalMsec(1000)
      .sourceId("index1")
      .type("type1")
      .build());
    crawlerParams.add(CrawlerParams.builder()
      .requestIntervalMsec(1500)
      .sourceId("index2")
      .type("type2")
      .build());

    ObjectMapper mapper = new ObjectMapper();
    String jsonValue = mapper.writeValueAsString(crawlerParams);

    CrawlersInstanceLoaderFromFileImpl crawlersInstanceLoaderFromFile = new CrawlersInstanceLoaderFromFileImpl("dummy") {
      @Override
      protected String getFileContents() throws Exception {
        return jsonValue;
      }
    };

    List<CrawlerParams> parsedParams = crawlersInstanceLoaderFromFile.getCrawlersInstances();
    Assert.assertEquals(2, parsedParams.size());
  }

  @Test
  public void testThatIsDoubleSourceInConfigThenExceptionWillBeThrown() throws Exception {
    List<CrawlerParams> crawlerParams = new ArrayList<>();
    crawlerParams.add(CrawlerParams.builder()
      .requestIntervalMsec(1000)
      .sourceId("index1")
      .type("type1")
      .build());
    crawlerParams.add(CrawlerParams.builder()
      .requestIntervalMsec(1500)
      .sourceId("index1")
      .type("type2")
      .build());

    ObjectMapper mapper = new ObjectMapper();
    String jsonValue = mapper.writeValueAsString(crawlerParams);

    CrawlersInstanceLoaderFromFileImpl crawlersInstanceLoaderFromFile = new CrawlersInstanceLoaderFromFileImpl("dummy"){
      @Override
      protected String getFileContents() throws Exception {
        return jsonValue;
      }
    };

    String errorText = "";
    try {
      List<CrawlerParams> parsedParams = crawlersInstanceLoaderFromFile.getCrawlersInstances();
    } catch (CrawlerException ce) {
      errorText = ce.getMessage();
    }
    Assert.assertEquals(errorText, "sourceId must be unique in all crawlers!");
  }


}
