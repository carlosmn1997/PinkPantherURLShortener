package urlshortener.team.domain;

import org.apache.commons.validator.routines.UrlValidator;
import java.net.HttpURLConnection;
import java.net.URL;

public class ValidUrl {

    private String url;

    public ValidUrl(String url) {
        this.url = url;
    }

    public boolean checkSyntax() {
        UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
        return urlValidator.isValid(url);
    }

    public boolean checkAlive() {
        if(!checkSyntax()) {
            return false;
        }
        else {
            try {
                URL u = new URL(url);
                HttpURLConnection h = (HttpURLConnection) u.openConnection();
                h.setRequestMethod("HEAD");
                return h.getResponseCode() == 200;
            }
            catch(Exception e) {
                return false;
            }
        }
    }
}
