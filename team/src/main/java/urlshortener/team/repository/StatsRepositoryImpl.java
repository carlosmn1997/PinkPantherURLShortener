package urlshortener.team.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import urlshortener.team.domain.Stats;
import urlshortener.team.domain.UriStats;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Optional;

public class StatsRepositoryImpl implements StatsRepository {

  private static final Logger log = LoggerFactory
          .getLogger(StatsRepositoryImpl.class);
  private JdbcTemplate jdbc;

  public StatsRepositoryImpl(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public Stats getStats() {
    // https://stackoverflow.com/questions/6431607/get-application-uptime
    RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
    long uptime = runtime.getUptime() / 1000;

    long availableMemory = Runtime.getRuntime().freeMemory();
    long usedMemory = Runtime.getRuntime().maxMemory() - availableMemory;

    Timestamp lastRedirection = new Timestamp(0L);
    long clickNumber = 0;
    long uriNumber = 0;
    long userNumber = 0;
    try {
      clickNumber = jdbc
              .queryForObject("select count(*) from click", Long.class);
      uriNumber = jdbc.queryForObject("select count(*) from shorturl",
              Long.class);
      userNumber = jdbc
              .queryForObject("select count(*) from (select ip from click group by ip)",
                      Long.class);
      lastRedirection = new Timestamp(jdbc
              .queryForObject("select CREATED from click order by id desc limit 1",
                      Timestamp.class).getTime());
    } catch (Exception e) {
      log.debug("When select ", e);
    }

    return new Stats(uptime, userNumber, uriNumber,
            clickNumber, lastRedirection, usedMemory, availableMemory);
  }

  public Optional<UriStats> getUriStats(String hash) {
    ClickRepository clickRepository = new ClickRepositoryImpl(jdbc);
    ShortURLRepository urlRepository = new ShortURLRepositoryImpl(jdbc);
    Long clicks;
    Date creationDate;
    try {
      clicks = clickRepository.clicksByHash(hash);
      creationDate = urlRepository.findByKey(hash).getCreated();
    } catch (Exception e) {
      // Exception selecting creation date -> hash not found
      log.debug("When select ", e);
      return Optional.empty();
    }
    try {
      Timestamp lastRedirection = new Timestamp(0);
      lastRedirection = new Timestamp(jdbc
              .queryForObject("select CREATED from click order by id desc limit 1",
                      Timestamp.class).getTime());
      return Optional.of(new UriStats(creationDate, clicks, lastRedirection));
    } catch (Exception e) {
      // Exception selecting last redirection -> hash found but no clicks found
      log.debug("When select ", e);
      return Optional.of(new UriStats(creationDate, clicks, new Timestamp(0L)));
    }

  }
}
