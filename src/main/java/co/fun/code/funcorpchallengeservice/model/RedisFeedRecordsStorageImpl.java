package co.fun.code.funcorpchallengeservice.model;

import co.fun.code.generatedservice.model.ExtendedFeedRecord;
import co.fun.code.generatedservice.model.FeedRecord;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class RedisFeedRecordsStorageImpl extends JedisPooledConnector implements IFeedRecordsStorage {
  private final static String RECORDS_SET = "records.index";
  private final static int MAX_LIMIT_PER_PAGE = 1000;
  private final ObjectMapper mapper;

  public RedisFeedRecordsStorageImpl(@Value("${redis.host:localhost}") String redisHost, @Value("${redis.port:6379}") int redisPort) {
    super(redisHost, redisPort);
    mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public boolean isRecordExists(String id) {
    return getJedis().exists(id);
  }

  @Override
  public ExtendedFeedRecord getRecordById(String id) throws Exception{
    String body = getJedis().get(id);
    log.debug("read json for record: {}", body);
    return mapper.readValue(body, ExtendedFeedRecord.class);
  }

  @Override
  public void storeRecord(ExtendedFeedRecord record) throws Exception {
    String jsonBody = mapper.writeValueAsString(record);
    Transaction t = getJedis().multi();
    t.set(record.getId(), jsonBody);
    t.zadd(RECORDS_SET, record.getTimestamp().doubleValue(), record.getId());
    t.exec();
  }

  @Override
  public List<FeedRecord> getRecords(RecordSearchParams searchParams) throws Exception {
    List<FeedRecord> recordList = new ArrayList<>();
    int offset = searchParams.getPaginationParams().getOffset();
    int limit = searchParams.getPaginationParams().getLimit();
    log.debug("getRecords, offset: {}, limit: {}", offset, limit);
    if (offset < 0) {
      offset = 0;
    }
    if (limit <= 0 || limit > MAX_LIMIT_PER_PAGE) {
      limit = MAX_LIMIT_PER_PAGE;
    }
    int days = searchParams.getDaysInHistory();
    if (days <= 0) {
      days = 1;
    } else if (days >= 300) {
      days = 300;
    }
    long maxtime = System.currentTimeMillis();
    long mintime = maxtime - days * 86400L * 1000L;
    Jedis jedis = getJedis();
    Set<String> result = jedis.zrevrangeByScore(RECORDS_SET, maxtime, mintime, offset, limit);
    if (result.size() > 0 ) {
      String[] sarray = new String[result.size()];
      result.toArray(sarray);
      List<String> mgetList = jedis.mget(sarray);
      mgetList.forEach(s -> {
        try {
          FeedRecord record = mapper.readValue(s, FeedRecord.class);
          recordList.add(record);
        } catch (Exception e) {
          log.warn("readValue exception", e);
        }
      });
    }
    return recordList;
  }
}
