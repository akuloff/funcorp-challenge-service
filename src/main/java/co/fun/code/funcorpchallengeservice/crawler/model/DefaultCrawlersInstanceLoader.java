package co.fun.code.funcorpchallengeservice.crawler.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public abstract class DefaultCrawlersInstanceLoader implements ICrawlersInstanceLoader {
  protected abstract String getConfigBody() throws Exception;
  private final ICrawlerInstanceStorer storer;

  public DefaultCrawlersInstanceLoader(ICrawlerInstanceStorer storer) {
    this.storer = storer;
  }

  @Override
  public void load() throws Exception {
    List<CrawlerParams> paramsList = new ArrayList<>();
    String allBody = getConfigBody();
    log.debug("config body: {}", allBody);
    if (!StringUtils.isEmpty(allBody)) {
      ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      CrawlerParams[] paramsArray = objectMapper.readValue(allBody, CrawlerParams[].class);
      paramsList = Arrays.asList(paramsArray);

      for (CrawlerParams p : paramsList) {
        if (storer.isContains(p.getSourceId())) {
          throw new CrawlerException("sourceId must be unique in all crawlers!");
        } else {
          CrawlerApiConnection apiConnection = p.getApiConnection();
          if (apiConnection != null) {
            Field[] fields = CrawlerApiConnection.class.getDeclaredFields();
            for (Field field : fields) {
              if (field.getType().equals(String.class)) {
                field.setAccessible(true);
                String fvalue = (String) field.get(apiConnection);
                if (fvalue != null && fvalue.startsWith("$")) {
                  String paramName = fvalue.replace("$", "");
                  String env = System.getenv(paramName) != null ? System.getenv(paramName) : System.getProperty(paramName);
                  if (!StringUtils.isEmpty(env)) {
                    field.set(apiConnection, env);
                  }
                }
              }
            }
          }
          storer.addParams(p);
        }
      }
    }
  }
}
