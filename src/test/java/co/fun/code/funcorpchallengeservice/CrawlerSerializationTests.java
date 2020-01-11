package co.fun.code.funcorpchallengeservice;

import co.fun.code.funcorpchallengeservice.crawler.model.CrawlerParams;
import co.fun.code.funcorpchallengeservice.crawler.model.CrawlersInstanceLoaderFromFileImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class CrawlerSerializationTests {

  @Test
  public void testParamsSerializationWithAnnotationName() throws Exception {
    CrawlerParams params = CrawlerParams.builder()
      .requestIntervalMsec(3000)
      .indexName("index_123")
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
      .indexName("index1")
      .type("type1")
      .build());
    crawlerParams.add(CrawlerParams.builder()
      .requestIntervalMsec(1500)
      .indexName("index2")
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

    List<CrawlerParams> parsedParams = crawlersInstanceLoaderFromFile.getCrawlersInstances();
    Assert.assertEquals(2, parsedParams.size());
  }

}
