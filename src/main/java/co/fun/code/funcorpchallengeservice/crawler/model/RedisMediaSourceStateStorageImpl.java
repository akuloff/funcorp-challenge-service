package co.fun.code.funcorpchallengeservice.crawler.model;

import co.fun.code.funcorpchallengeservice.model.JedisPooledConnector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class RedisMediaSourceStateStorageImpl extends JedisPooledConnector implements IMediaSourceStateStorage {
  private static final String KEY_PREFIX = "media-state-";
  private final ObjectMapper mapper;

  public RedisMediaSourceStateStorageImpl(@Value("${redis.host:localhost}") String redisHost, @Value("${redis.port:6379}") int redisPort) {
    super(redisHost, redisPort);
    mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public MediaSourceState getStateForId(String id) throws Exception {
    String body = getJedis().get(KEY_PREFIX + id);
    log.debug("getStateForId body: {}", body);
    if (!StringUtils.isEmpty(body)) {
      return mapper.readValue(body, MediaSourceState.class);
    } else {
      return null;
    }
  }

  @Override
  public void updateState(MediaSourceState state) throws Exception {
    getJedis().set(KEY_PREFIX + state.getId(), mapper.writeValueAsString(state));
  }
}
