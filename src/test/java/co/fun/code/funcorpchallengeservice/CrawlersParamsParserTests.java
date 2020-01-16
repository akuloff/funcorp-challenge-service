package co.fun.code.funcorpchallengeservice;

import co.fun.code.funcorpchallengeservice.crawler.model.CrawlerApiConnection;
import co.fun.code.funcorpchallengeservice.crawler.model.CrawlerException;
import co.fun.code.funcorpchallengeservice.crawler.model.CrawlerParams;
import co.fun.code.funcorpchallengeservice.crawler.model.CrawlersInstanceLoaderFromFileImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrawlersParamsParserTests {
  private String jsonValue;
  private CrawlersInstanceLoaderFromFileImpl crawlersInstanceLoaderFromFile = new CrawlersInstanceLoaderFromFileImpl("dummy") {
    @Override
    protected String getConfigBody() throws Exception {
      return jsonValue;
    }
  };

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
    jsonValue = mapper.writeValueAsString(crawlerParams);

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
    jsonValue = mapper.writeValueAsString(crawlerParams);

    String errorText = "";
    try {
      List<CrawlerParams> parsedParams = crawlersInstanceLoaderFromFile.getCrawlersInstances();
    } catch (CrawlerException ce) {
      errorText = ce.getMessage();
    }
    Assert.assertEquals(errorText, "sourceId must be unique in all crawlers!");
  }

  @Test
  public void testMultipleParamsSerialization() throws Exception {
    List<CrawlerParams> crawlerParams = new ArrayList<>();
    crawlerParams.add(CrawlerParams.builder()
      .requestIntervalMsec(2000)
      .sourceId("giphy1")
      .type("giphy")
      .searchQuery("funny gifs")
      .tags(Arrays.asList("tag1", "tag2"))
      .language("de")
      .maxHistoryDays(5)
      .maxHistoryRecords(100)
      .apiConnection(CrawlerApiConnection.builder()
        .apiKey("apikey123")
        .build())
      .build());
    crawlerParams.add(CrawlerParams.builder()
      .requestIntervalMsec(3000)
      .sourceId("coub1")
      .type("coub")
      .searchQuery("funny memes")
      .language("de")
      .maxHistoryDays(10)
      .apiConnection(CrawlerApiConnection.builder()
        .apiKey("apikey333")
        .userName("coubUser123")
        .userPasswd("$userpass2")
        .build())
      .build());

    ObjectMapper mapper = new ObjectMapper();
    jsonValue = mapper.writeValueAsString(crawlerParams);
    Assert.assertNotNull(jsonValue);
  }

  @Test
  public void testThatApiParamsValuesFromEnvironmentVariables() throws Exception {
    List<CrawlerParams> crawlerParams = new ArrayList<>();
    crawlerParams.add(CrawlerParams.builder()
      .requestIntervalMsec(3000)
      .sourceId("coub2")
      .type("coub")
      .searchQuery("funny memes")
      .language("de")
      .maxHistoryDays(10)
      .apiConnection(CrawlerApiConnection.builder()
        .userName("$username1")
        .userPasswd("$userpass1")
        .build())
      .build());
    ObjectMapper mapper = new ObjectMapper();
    jsonValue = mapper.writeValueAsString(crawlerParams);
    System.setProperty("username1", "env_username");
    List<CrawlerParams> parsedParams = crawlersInstanceLoaderFromFile.getCrawlersInstances();
    Assert.assertEquals("env_username", parsedParams.get(0).getApiConnection().getUserName());
  }


}
