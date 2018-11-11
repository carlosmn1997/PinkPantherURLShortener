package urlshortener.team.repository.fixture;

import urlshortener.team.domain.Stats;

public class StatsFixture {
    public static Stats stats(){
        return new Stats(1L,1L,1L,1L,1L,1L,1L);
    }
}
