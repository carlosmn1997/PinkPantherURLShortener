package urlshortener.team.service;

import org.springframework.http.ResponseEntity;
import urlshortener.team.domain.ShortURL;

public interface ShortUrlService {
    ShortURL createAndSaveShortUrl(String target, String sponsor, String ip, Boolean periodicity, Boolean qr);
    void createAndSaveClick(String hash, String ip);
    ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l);
}
