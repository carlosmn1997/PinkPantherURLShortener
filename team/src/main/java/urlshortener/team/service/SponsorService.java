package urlshortener.team.service;

import urlshortener.team.domain.ShortURL;

public interface SponsorService {
    String getDefaultSponsorUri();
    String generateHtml(ShortURL l);

}
