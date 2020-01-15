package co.fun.code.funcorpchallengeservice.crawler.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class RedisMediaSourceStateStorageImpl implements IMediaSourceStateStorage {
  private static final String KEY_PREFIX = "media-state-";
  private final String redisHost;
  private final int redisPort;
  private Jedis jedis;
  private final ObjectMapper mapper;

  public RedisMediaSourceStateStorageImpl(@Value("${redis.host:localhost}") String redisHost, @Value("${redis.port:6379}") int redisPort) {
    this.redisHost = redisHost;
    this.redisPort = redisPort;
    mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @PostConstruct
  public void init() {
    jedis = new Jedis(redisHost, redisPort);
  }

  @Override
  public MediaSourceState getStateForId(String id) throws Exception {
    String body = jedis.get(KEY_PREFIX + id);
    log.debug("getStateForId body: {}", body);
    if (!StringUtils.isEmpty(body)) {
      return mapper.readValue(body, MediaSourceState.class);
    } else {
      return null;
    }
  }

  @Override
  public void updateState(MediaSourceState state) throws Exception {
    jedis.set(KEY_PREFIX + state.getId(), mapper.writeValueAsString(state));
  }
}
