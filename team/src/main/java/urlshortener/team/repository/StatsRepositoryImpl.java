package urlshortener.team.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import urlshortener.team.domain.Stats;

import java.util.ArrayList;
import java.util.List;

@Repository
public class StatsRepositoryImpl implements StatsRepository {

    private JdbcTemplate jdbc;

    public StatsRepositoryImpl(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public Stats getStats(){
        return new Stats(1L,1L,1L,
                1L,1L,1L,1L);
    }
}
