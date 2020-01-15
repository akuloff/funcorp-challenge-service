package co.fun.code.funcorpchallengeservice.service;

import co.fun.code.generatedservice.api.RecordApiController;
import co.fun.code.generatedservice.model.ExtendedFeedRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
public class MyRecordApiController extends RecordApiController {

  public MyRecordApiController(NativeWebRequest request) {
    super(request);
  }

  @Override
  public ResponseEntity<ExtendedFeedRecord> recordGet(@NotNull @Valid String id) {
    return super.recordGet(id);
  }

}
