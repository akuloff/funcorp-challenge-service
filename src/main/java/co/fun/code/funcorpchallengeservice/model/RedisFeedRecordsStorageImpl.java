package co.fun.code.funcorpchallengeservice.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class RedisFeedRecordsStorageImpl implements IFeedRecordsStorage {

  @Value("${redis_host:localhost}")
  private String redisHost;

  @Value("${redis_port:6379}")
  private int redisPort;

  private Jedis jedis;

  @PostConstruct
  public void init(){
    jedis = new Jedis(redisHost, redisPort);
  }

  @Override
  public boolean isRecordExixts(String id) {
    return jedis.exists(id);
  }

  @Override
  public ExtendedFeedRecord getRecordById(String id) {
    String val = jedis.get(id);
    return null;
  }

  @Override
  public void storeRecord(ExtendedFeedRecord record) throws Exception {

  }
}
