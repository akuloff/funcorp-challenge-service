package co.fun.code.funcorpchallengeservice.service;

import co.fun.code.funcorpchallengeservice.model.IFeedRecordsStorage;
import co.fun.code.funcorpchallengeservice.model.PaginationParams;
import co.fun.code.funcorpchallengeservice.model.RecordSearchParams;
import co.fun.code.generatedservice.api.FeedApiController;
import co.fun.code.generatedservice.model.FeedRecord;
import co.fun.code.generatedservice.model.FeedResponse;
import co.fun.code.generatedservice.model.MetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Controller
public class MyFeedApiController extends FeedApiController {
  @Autowired
  private IFeedRecordsStorage feedRecordsStorage;

  public MyFeedApiController(NativeWebRequest request) {
    super(request);
  }

  @Override
  public ResponseEntity<FeedResponse> feedGet(@Valid String language, @Min(1) @Max(100) @Valid Integer limit, @Min(0) @Valid Integer offset, @Valid String cursor, @Valid String sinceId, @Valid Long sinceTimestamp, @Valid Integer daysInHistory) {

    RecordSearchParams searchParams = RecordSearchParams.builder()
      .daysInHistory(daysInHistory != null ? daysInHistory : 1)
      .fromTimestamp(sinceTimestamp != null ? sinceTimestamp : 0L)
      .paginationParams(PaginationParams.builder()
        .limit(limit)
        .offset(offset)
        .cursor(cursor)
        .build())
      .build();

    try {
      List<FeedRecord> records = feedRecordsStorage.getRecords(searchParams);

      FeedResponse feedResponse  = new FeedResponse();
      feedResponse.setRecords(records);

      //TODO Доделать пажинацию

      MetaData metaData = new MetaData();
      metaData.setTotalRecords(records.size());
      metaData.setPage(1);
      metaData.setLimit(limit);

      feedResponse.setMetadata(metaData);

      return ResponseEntity.ok()
        .body(feedResponse);

    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
    }
  }
}
