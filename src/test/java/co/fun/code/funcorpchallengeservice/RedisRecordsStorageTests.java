package co.fun.code.funcorpchallengeservice;

import co.fun.code.funcorpchallengeservice.model.PaginationParams;
import co.fun.code.funcorpchallengeservice.model.RecordSearchParams;
import co.fun.code.funcorpchallengeservice.model.RedisFeedRecordsStorageImpl;
import co.fun.code.generatedservice.model.ExtendedFeedRecord;
import co.fun.code.generatedservice.model.FeedRecord;
import com.github.fppt.jedismock.RedisServer;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.util.List;

public class RedisRecordsStorageTests {
  private static RedisServer redisServer;
  private static RedisFeedRecordsStorageImpl storage;

  @BeforeAll
  public static void start() throws Exception {
    redisServer = RedisServer.newRedisServer(16379);
    redisServer.start();
    storage = new RedisFeedRecordsStorageImpl(redisServer.getHost(), redisServer.getBindPort());
    storage.init();
  }

  @AfterAll
  public static void stop() {
    redisServer.stop();
  }

  @Test
  public void testThatJedisEngineWorks() {
    Jedis jedis = new Jedis(redisServer.getHost(), redisServer.getBindPort());
    jedis.set("key1", "value1");
    String result = jedis.get("key1");
    Assert.assertEquals("value1", result);
  }

  @Test
  public void testRedisStorageSaveAndReadKeyValue() throws Exception{
    boolean isExists = storage.isRecordExists("recordId1");
    Assert.assertFalse(isExists);

    ExtendedFeedRecord feedRecord = new ExtendedFeedRecord();
    feedRecord.setId("recordId1");
    feedRecord.setTimestamp(System.currentTimeMillis());
    feedRecord.setDescription("test record");
    storage.storeRecord(feedRecord);

    isExists = storage.isRecordExists(feedRecord.getId());
    Assert.assertTrue(isExists);
  }

//  @Test
  //jedis-mock не поддерживает sorted set комманды
  public void testRedisStoragegetGetRecordsList() throws Exception{
    RedisFeedRecordsStorageImpl s2 = storage;
//    s2 = new RedisFeedRecordsStorageImpl("localhost", 6379);
//    s2.init();

    ExtendedFeedRecord feedRecord = new ExtendedFeedRecord();
    feedRecord.setId("recordId1");
    feedRecord.setTimestamp(System.currentTimeMillis());
    feedRecord.setDescription("test record");
    s2.storeRecord(feedRecord);

    feedRecord.setId("recordId2");
    feedRecord.setTimestamp(System.currentTimeMillis());
    feedRecord.setDescription("test record2");
    s2.storeRecord(feedRecord);

    RecordSearchParams searchParams = RecordSearchParams.builder()
      .daysInHistory(1)
      .paginationParams(PaginationParams.builder().offset(0).limit(10).build())
      .build();
    List<FeedRecord> recordList = s2.getRecords(searchParams);
    Assert.assertEquals(2, recordList.size());
  }
}
