package urlshortener.team.periodical;

import org.springframework.beans.factory.annotation.Autowired;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Scheduled(cron = "0 3 * * * *")
    public void task() {
        List<ShortURL> l = shortURLRepository.getAllToCheck();
        for(ShortURL i : l) {
            if(checkUri(i.getTarget())) { // reachable uri
                if(!i.isAliveOnLastCheck()) {
                    i.setAliveOnLastCheck(true);
                    shortURLRepository.update(i);
                }
            }
            else { // unreachable uri
                if(i.isAliveOnLastCheck()) {
                    i.setAliveOnLastCheck(false);
                    shortURLRepository.update(i);
                }
            }
        }
    }

    /**
     * Checks if the uri is active
     * @param u the uri to check
     * @return true if active, false if not active
     */
    public boolean checkUri(String u) {
        try {
            URL url = new URL(u);
            HttpURLConnection h = (HttpURLConnection) url.openConnection();
            h.setRequestMethod("HEAD");
            return h.getResponseCode() == 200;
        }
        catch(Exception e) {
            return false;
        }
    }
}
