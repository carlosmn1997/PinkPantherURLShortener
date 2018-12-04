package urlshortener.team.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.team.domain.Stats;
import urlshortener.team.domain.UriStats;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.StatsRepository;

import java.util.Optional;

@RestController
public class StatsController {
  @Autowired
  protected StatsRepository statsRepository;

  @Autowired
  protected ClickRepository clickRepository;

  @GetMapping("/stats")
  public ResponseEntity<Stats> getStats() {
    Stats serverStats = statsRepository.getStats();
    return new ResponseEntity<>(serverStats, HttpStatus.OK);
  }

  @GetMapping("/{id}/info")
  public ResponseEntity<UriStats> getUriStats(@PathVariable String id) {
//    return statsRepository.getUriStats(id)
//            .map(it -> new ResponseEntity<>(it, HttpStatus.OK))
//            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
      Optional<UriStats> result = statsRepository.getUriStats(id);

      if (result.isPresent()) {
        return new ResponseEntity<>(result.get(), HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
  }
}
