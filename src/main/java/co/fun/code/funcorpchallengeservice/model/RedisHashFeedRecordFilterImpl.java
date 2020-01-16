package co.fun.code.funcorpchallengeservice.model;

import co.fun.code.generatedservice.model.ExtendedFeedRecord;
import lombok.Getter;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.security.MessageDigest;
import java.util.Arrays;

@Component
public class RedisHashFeedRecordFilterImpl implements IFeedRecordFilter {
  private final int maxMediaDurationSec = 300; //max 5 min
  private final String redisHost;
  private final int redisPort;

  @Getter
  private final boolean fastHash;

  @Getter
  private Jedis jedis;

  private MessageDigest digest;

  public RedisHashFeedRecordFilterImpl(@Value("${redis.host:localhost}") String redisHost, @Value("${redis.port:6379}") int redisPort, @Value("${filter.use.fast.hash}") boolean fastHash) {
    this.redisHost = redisHost;
    this.redisPort = redisPort;
    this.fastHash = fastHash;
  }

  @PostConstruct
  public void init() throws Exception {
    jedis = new Jedis(redisHost, redisPort);
    digest = MessageDigest.getInstance("SHA-256");
  }

  @Override
  public boolean filtered(ExtendedFeedRecord record) {
    boolean filtered;
    if (record.getDuration() != null && record.getDuration() > maxMediaDurationSec ) {
      filtered = false;
    } else {
      String hashValue = "";
      if (record.getThumbnail() != null && record.getThumbnail().length > 0) {
        if (fastHash) {
          hashValue = String.valueOf(Arrays.hashCode(record.getThumbnail()));
        } else {
          hashValue = Hex.encodeHexString(digest.digest(record.getThumbnail()));
        }
      } else if (!StringUtils.isEmpty(record.getExternalId()) && !StringUtils.isEmpty(record.getSourceName())) {
        hashValue = record.getSourceName() + "_" + record.getExternalId();
      } else {
        hashValue = record.getId();
      }
      if (!StringUtils.isEmpty(hashValue)) {
        filtered = !jedis.exists(hashValue);
      } else {
        filtered = false;
      }
    }
    return filtered;
  }
}
