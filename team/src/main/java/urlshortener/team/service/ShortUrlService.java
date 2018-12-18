package urlshortener.team.service;

import org.springframework.http.ResponseEntity;
import urlshortener.team.domain.ShortURL;

import java.net.URI;

public interface ShortUrlService {
    ShortURL createAndSaveShortUrl(String target, URI uriBase, String sponsor, String ip, Boolean periodicity, Boolean qr);
    void createAndSaveClick(String hash, String ip);
    ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l);
}
