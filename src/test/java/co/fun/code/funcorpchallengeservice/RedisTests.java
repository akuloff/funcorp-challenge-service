package co.fun.code.funcorpchallengeservice;

import co.fun.code.funcorpchallengeservice.model.RedisFeedRecordsStorageImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

public class RedisTests {
  private static Jedis jedis;
  private static RedisFeedRecordsStorageImpl storage;

  @BeforeAll
  public static void init() {
    jedis = new Jedis("localhost", 6379);
    storage = new RedisFeedRecordsStorageImpl();
    storage.init();
    String s = "123";
  }

  @AfterAll
  public static void after(){

  }

  @Test
  public void testGetHash() {
    String result = jedis.hget("user:1", "id");
    String s = "123";
  }

  @Test
  public void testScan() {
    Set<String> result = jedis.zrevrangeByScore("news.index", 40, 15, 0, 10);
    String[] sarray = new String[result.size()];
    result.toArray(sarray);
    List<String> mgetResult = jedis.mget(sarray);
    String s2 = "123";
  }
}
