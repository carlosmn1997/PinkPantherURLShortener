package urlshortener.team.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import urlshortener.team.repository.*;
import urlshortener.team.service.JobService;
import urlshortener.team.service.JobServiceImpl;

@Configuration
public class ServiceContext {

  @Bean
  JobService jobService() { return new JobServiceImpl(); }

}
