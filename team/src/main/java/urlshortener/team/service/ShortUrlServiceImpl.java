package urlshortener.team.service;

import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import urlshortener.team.domain.Click;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.team.web.rest.UrlShortenerController;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.UUID;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class ShortUrlServiceImpl implements ShortUrlService {
    @Autowired
    ShortURLRepository shortURLRepository;

    @Autowired
    ClickRepository clickRepository;

    @Autowired
    UriService uriService;

    private static final Logger LOG = LoggerFactory
            .getLogger(UrlShortenerController.class);

    @Override
    public ShortURL createAndSaveShortUrl(String target, String sponsor,
                                          String ip, Boolean periodicity, Boolean qr) {
        String hash = Hashing.murmur3_32().hashString(target + sponsor + ip
                + periodicity.toString() + qr.toString(), StandardCharsets.UTF_8)
                .toString();
        URI uri =linkTo(methodOn(UrlShortenerController.class)
                .redirectTo(hash, null)).toUri();
        Date created = new Date(System.currentTimeMillis());
        String owner = UUID.randomUUID().toString();
        Integer mode = HttpStatus.TEMPORARY_REDIRECT.value();
        boolean safe = true;
        String country = null;
        boolean aliveOnLastCheck = periodicity ?  uriService.checkAlive(target) : false;
        return shortURLRepository.save(new ShortURL(hash, target, uri, sponsor, created,
                owner, mode, safe, ip, country, periodicity, aliveOnLastCheck, qr));
    }

    @Override
    public void createAndSaveClick(String hash, String ip) {
        Click cl = new Click(null, hash, new Date(System.currentTimeMillis()),
                null, null, null, ip, null);
        cl = clickRepository.save(cl);
        LOG.info(cl != null ? "[" + hash + "] saved with id [" + cl.getId() + "]" : "[" + hash + "] was not saved");
    }

    @Override
    public ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create(l.getTarget()));
        return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
    }
}
