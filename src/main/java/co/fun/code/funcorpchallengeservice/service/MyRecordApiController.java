package co.fun.code.funcorpchallengeservice.service;

import co.fun.code.funcorpchallengeservice.model.IFeedRecordsStorage;
import co.fun.code.generatedservice.api.RecordApiController;
import co.fun.code.generatedservice.model.ExtendedFeedRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
public class MyRecordApiController extends RecordApiController {
  @Autowired
  IFeedRecordsStorage recordsStorage;

  public MyRecordApiController(NativeWebRequest request) {
    super(request);
  }

  @Override
  public ResponseEntity<ExtendedFeedRecord> recordGet(@NotNull @Valid String id) {
    try {
      return ResponseEntity.ok()
        .body(recordsStorage.getRecordById(id));
    }  catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
    }
  }

}
