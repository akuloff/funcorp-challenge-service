package co.fun.code.funcorpchallengeservice;

import co.fun.code.funcorpchallengeservice.model.RedisHashFeedRecordFilterImpl;
import co.fun.code.generatedservice.model.ExtendedFeedRecord;
import com.github.fppt.jedismock.RedisServer;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FeedRecordFilterTests {
  private static RedisServer redisServer;
  private static RedisHashFeedRecordFilterImpl filter;

  @BeforeAll
  public static void start() throws Exception {
    redisServer = RedisServer.newRedisServer(16389);
    redisServer.start();
    filter = new RedisHashFeedRecordFilterImpl(redisServer.getHost(), redisServer.getBindPort(), false);
    filter.init();
  }

  @AfterAll
  public static void stop() {
    redisServer.stop();
  }

  @Test
  public void testThatIfRecordWithIdNotPresentInStorageThenFiltered() throws Exception{
    ExtendedFeedRecord feedRecord = new ExtendedFeedRecord();
    feedRecord.setMediaLinkId("l123");
    feedRecord.setId("filterRecordId1");
    feedRecord.setTimestamp(System.currentTimeMillis());
    feedRecord.setDescription("filter test record");
    boolean filtered = filter.filtered(feedRecord);
    Assert.assertTrue(filtered);
  }

  @Test
  public void testThatIfRecordWithIdIsPresentInStorageThenNotFiltered() throws Exception{
    ExtendedFeedRecord feedRecord = new ExtendedFeedRecord();
    feedRecord.setMediaLinkId("l123");
    feedRecord.setId("filterRecordId2");
    feedRecord.setTimestamp(System.currentTimeMillis());
    feedRecord.setDescription("filter test record 2");
    filter.getJedis().set(feedRecord.getId(), "123");
    boolean filtered = filter.filtered(feedRecord);
    Assert.assertFalse(filtered);
  }

  //TODO add thumbnail tests
  //TODO add media duration test
}
