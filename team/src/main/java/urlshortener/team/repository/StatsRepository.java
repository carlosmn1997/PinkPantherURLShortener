package urlshortener.team.repository;

import urlshortener.team.domain.Stats;
import urlshortener.team.domain.UriStats;

import java.util.Optional;

public interface StatsRepository {

  Stats getStats();

  Optional<UriStats> getUriStats(String hash);
}
