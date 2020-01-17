package co.fun.code.funcorpchallengeservice.crawler.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;

@Component
@Slf4j
public class RedisMediaSourceStateStorageImpl implements IMediaSourceStateStorage {
  private static final String KEY_PREFIX = "media-state-";
  private final String redisHost;
  private final int redisPort;

  //private Jedis jedis;
  private final ObjectMapper mapper;

  private JedisPoolConfig poolConfig;
  private JedisPool jedisPool;

  public RedisMediaSourceStateStorageImpl(@Value("${redis.host:localhost}") String redisHost, @Value("${redis.port:6379}") int redisPort) {
    this.redisHost = redisHost;
    this.redisPort = redisPort;
    mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  private Jedis getJedis(){
    //return jedis;
    return jedisPool.getResource();
  }

  private JedisPoolConfig buildPoolConfig() {
    final JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(128);
    poolConfig.setMaxIdle(128);
    poolConfig.setMinIdle(16);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    poolConfig.setTestWhileIdle(true);
    poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
    poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
    poolConfig.setNumTestsPerEvictionRun(3);
    poolConfig.setBlockWhenExhausted(true);
    return poolConfig;
  }

  @PostConstruct
  public void init() {
    //jedis = new Jedis(redisHost, redisPort);
    poolConfig = buildPoolConfig();
    jedisPool = new JedisPool(poolConfig, redisHost, redisPort);
  }

  @PreDestroy
  public void close() {
//    if (jedis != null) {
//      jedis.close();
//    }
    if (jedisPool != null) {
      jedisPool.close();
    }
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
