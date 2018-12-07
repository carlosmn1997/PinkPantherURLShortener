package urlshortener.team.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;

import java.util.List;

/**
 * Implements a procedure that checks periodically the status of
 * the urls
 */
@Component
public class AliveServiceImpl implements AliveService {
  @Autowired
  protected ShortURLRepository shortURLRepository;

  @Override
  @Scheduled(cron = "0 3 * * * *")
  public void task() {
    List<ShortURL> l = shortURLRepository.getAllToCheck();
    for (ShortURL i : l) {
      UriServiceImpl v = new UriServiceImpl();
      if (v.checkAlive(i.getTarget())) { // reachable uri
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
