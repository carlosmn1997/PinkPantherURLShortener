package urlshortener.team.repository.fixture;

import urlshortener.team.domain.Stats;

import java.sql.Timestamp;

public class StatsFixture {
  public static Stats stats() {
    return new Stats(1L, 1L, 1L, 1L, new Timestamp(1L), 1L, 1L);
  }
}
