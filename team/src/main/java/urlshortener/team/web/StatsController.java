package urlshortener.team.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.domain.Stats;
import urlshortener.team.domain.UriStats;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.team.repository.StatsRepository;

import javax.servlet.http.HttpServletRequest;

@RestController
public class StatsController {
    @Autowired
    protected StatsRepository statsRepository;

    @Autowired
    protected ClickRepository clickRepository;

    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public ResponseEntity<Stats> getStats(HttpServletRequest request) {

        Stats serverStats = statsRepository.getStats();

        return new ResponseEntity<>(serverStats,HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/info", method = RequestMethod.GET)
    public ResponseEntity<UriStats> getUriStats(@PathVariable String id,
                                             HttpServletRequest request) {

        try {
            UriStats uriStats = statsRepository.getUriStats(id);
            return new ResponseEntity<>(uriStats,HttpStatus.OK);
        } catch(NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
