package urlshortener.team.domain;

import org.apache.commons.validator.routines.UrlValidator;

public class ValidUrl {

    private String url;

    public ValidUrl(String url) {
        this.url = url;
    }

    public boolean check() {
        UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
        return urlValidator.isValid(url);
    }
}
