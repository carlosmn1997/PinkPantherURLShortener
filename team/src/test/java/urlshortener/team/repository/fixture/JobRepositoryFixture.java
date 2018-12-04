package urlshortener.team.repository.fixture;

import urlshortener.team.domain.Job;

public class JobRepositoryFixture {

  public static Job jobExample() {
    // String hash, int converted, int total, URI uriResult, List<CsvFormat> result
    return new Job("0", 2, 10, null, null);
  }

  public static Job jobWithUris() {
    // String hash, int converted, int total, URI uriResult, List<CsvFormat> result
    return new Job("1", 0, 2, null, null);
  }
}
