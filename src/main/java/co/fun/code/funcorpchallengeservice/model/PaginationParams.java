package co.fun.code.funcorpchallengeservice.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaginationParams {
  private String cursor;
  private int page;
  private int pageLimit;
}
