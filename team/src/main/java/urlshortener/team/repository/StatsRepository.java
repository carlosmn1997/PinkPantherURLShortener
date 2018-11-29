package urlshortener.team.repository;

import urlshortener.team.domain.Stats;
import urlshortener.team.domain.UriStats;

import java.util.List;

public interface StatsRepository {

    Stats getStats();

    UriStats getUriStats(String hash);
}
