package urlshortener.team.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import urlshortener.team.service.*;

@Configuration
public class ServiceContext {

  @Bean
  JobService jobService() { return new JobServiceImpl(); }

  @Bean
  UriService uriService() { return new UriServiceImpl(); }

  @Bean
  ShortUrlService shortUrlService() { return new ShortUrlServiceImpl(); }

  @Bean
  SponsorService sponsorService() { return new SponsorServiceImpl(); }

}
