package co.fun.code.funcorpchallengeservice.service;

import co.fun.code.funcorpchallengeservice.model.IMediaStorage;
import co.fun.code.funcorpchallengeservice.model.MediaContent;
import co.fun.code.generatedservice.api.MediaApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
public class MyMediaApiController extends MediaApiController {

  @Autowired
  private IMediaStorage mediaStorage;

  public MyMediaApiController(NativeWebRequest request) {
    super(request);
  }

  @Override
  public ResponseEntity<Resource> mediaGet(@NotNull @Valid String linkid) {
    try {
      MediaContent mediaContent = mediaStorage.getMediaContent(linkid);
      InputStreamResource resource = new InputStreamResource(mediaContent.getInputStream());
      return ResponseEntity.ok()
        .contentLength(mediaContent.getSize())
        .contentType(MediaType.parseMediaType("application/octet-stream"))
        .body(resource);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
    }
  }
}
