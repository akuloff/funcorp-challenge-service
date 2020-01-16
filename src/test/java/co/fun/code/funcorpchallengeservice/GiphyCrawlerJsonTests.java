package co.fun.code.funcorpchallengeservice;

import co.fun.code.funcorpchallengeservice.crawler.giphy.model.GiphySearchResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import static co.fun.code.funcorpchallengeservice.Library.getStringFromStream;

public class GiphyCrawlerJsonTests {
  private static ResourceLoader resourceLoader;
  private static ObjectMapper mapper;

  @BeforeAll
  public static void init(){
    resourceLoader = new DefaultResourceLoader();
    mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Test
  public void testThatJsonResponseParsed() throws Exception{
    Resource resource = resourceLoader.getResource("classpath:giphy-sample-search-1.json");
    String fileBody = getStringFromStream(resource.getInputStream());
    GiphySearchResponse response = mapper.readValue(fileBody, GiphySearchResponse.class);
    Assert.assertEquals("c5e71e263c475beb7ad3635e6a129ceaa6034de7", response.getMeta().getResponseId());
  }
}
