package co.fun.code.funcorpchallengeservice.model;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;

public class JedisPooledConnector {
  private final String redisHost;
  private final int redisPort;
  private JedisPool jedisPool;

  public JedisPooledConnector(String redisHost, int redisPort) {
    this.redisHost = redisHost;
    this.redisPort = redisPort;
  }

  public Jedis getJedis(){
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
  public void init() throws Exception {
    JedisPoolConfig poolConfig = buildPoolConfig();
    jedisPool = new JedisPool(poolConfig, redisHost, redisPort);
  }

  @PreDestroy
  public void close() {
    if (jedisPool != null) {
      jedisPool.close();
    }
  }

}
