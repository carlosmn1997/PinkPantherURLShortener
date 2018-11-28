package urlshortener.team.periodical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import urlshortener.team.repository.ShortURLRepositoryImpl;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Implements a procedure that checks periodically the status of
 * the urls
 */
@Component
public class Alive {
    @Autowired
    protected ShortURLRepository shortURLRepository;

    private static final Logger log = LoggerFactory
            .getLogger(ShortURLRepositoryImpl.class);

    @Scheduled(cron = "0 3 * * * *")
    public void task() {
        List<ShortURL> l = shortURLRepository.getAllToCheck();
        for(ShortURL i : l) {
            try {
                URL url = new URL(i.getTarget());
                HttpURLConnection h = (HttpURLConnection) url.openConnection();
                h.setRequestMethod("HEAD");

                // Chequeamos si no existe o sí
                if(h.getResponseCode() != 200) { // La uri no es alcanzable
                    if(i.isAliveOnLastCheck()) {
                        i.setAliveOnLastCheck(false);
                        shortURLRepository.update(i);
                    }
                }
                else { // La uri es alcanzable
                    if(!i.isAliveOnLastCheck()) {
                        i.setAliveOnLastCheck(true);
                        shortURLRepository.update(i);
                    }
                }
            }
            catch(Exception e) { // Uri no alcanzable
                if(i.isAliveOnLastCheck()) {
                    i.setAliveOnLastCheck(false);
                    shortURLRepository.update(i);
                }
            }
        }
    }
}
