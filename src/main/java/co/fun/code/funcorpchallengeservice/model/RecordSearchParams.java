package co.fun.code.funcorpchallengeservice.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecordSearchParams {
  private PaginationParams paginationParams;
}
