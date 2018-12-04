package urlshortener.team.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.domain.ValidUrl;
import urlshortener.team.repository.ShortURLRepository;

import java.util.List;

/**
 * Implements a procedure that checks periodically the status of
 * the urls
 */
@Component
public class AliveService {
  @Autowired
  protected ShortURLRepository shortURLRepository;

  @Scheduled(cron = "0 3 * * * *")
  public void task() {
    List<ShortURL> l = shortURLRepository.getAllToCheck();
    for (ShortURL i : l) {
      ValidUrl v = new ValidUrl(i.getTarget());
      if (v.checkAlive()) { // reachable uri
        if (!i.isAliveOnLastCheck()) {
          i.setAliveOnLastCheck(true);
          shortURLRepository.update(i);
        }
      } else { // unreachable uri
        if (i.isAliveOnLastCheck()) {
          i.setAliveOnLastCheck(false);
          shortURLRepository.update(i);
        }
      }
    }
  }
}
