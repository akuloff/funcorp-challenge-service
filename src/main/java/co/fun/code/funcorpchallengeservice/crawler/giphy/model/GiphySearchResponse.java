package co.fun.code.funcorpchallengeservice.crawler.giphy.model;

import lombok.Getter;

import java.util.List;

@Getter
public class GiphySearchResponse {
  private List<GiphyDataRecord> data;
  private GiphyPagination pagination;
  private GiphyMeta meta;
}
