package co.fun.code.funcorpchallengeservice.service;

import co.fun.code.funcorpchallengeservice.model.IFeedRecordsStorage;
import co.fun.code.generatedservice.api.FeedApiController;
import co.fun.code.generatedservice.model.FeedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Controller
public class MyFeedApiController extends FeedApiController {
  @Autowired
  private IFeedRecordsStorage feedRecordsStorage;

  public MyFeedApiController(NativeWebRequest request) {
    super(request);
  }

  @Override
  public ResponseEntity<FeedResponse> feedGet(@Valid String language, @Min(1) @Max(100) @Valid Integer limit, @Min(0) @Valid Integer offset, @Valid String cursor, @Valid String sinceId, @Valid Integer sinceTimestamp) {
    return null;
  }
}
