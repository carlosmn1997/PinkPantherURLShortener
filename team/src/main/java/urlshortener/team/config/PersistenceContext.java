package urlshortener.team.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import urlshortener.team.repository.*;
import urlshortener.team.service.JobService;
import urlshortener.team.service.JobServiceImpl;

@Configuration
public class PersistenceContext {

  @Autowired
  protected JdbcTemplate jdbc;

  @Bean
  ShortURLRepository shortURLRepository() {
    return new ShortURLRepositoryImpl(jdbc);
  }

  @Bean
  ClickRepository clickRepository() {
    return new ClickRepositoryImpl(jdbc);
  }

  @Bean
  JobRepository jobRepository() {
    return new JobRepositoryImpl(jdbc);
  }

  @Bean
  QRRepository qrRepository() {
    return new QRRepositoryImpl(jdbc);
  }

  @Bean
  StatsRepository statsRepository() {
    return new StatsRepositoryImpl(jdbc);
  }

  @Bean
  JobService jobService() { return new JobServiceImpl(); }

}
