package urlshortener.team.service;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.HttpURLConnection;
import java.net.URL;

public class UriServiceImpl implements UriService {

  @Override
  public boolean checkSyntax(String url) {
    UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});
    return urlValidator.isValid(url);
  }

  @Override
  public boolean checkAlive(String url) {
    if (!checkSyntax(url)) {
      return false;
    } else {
      try {
        //URL u = new URL(url);
        //HttpURLConnection h = (HttpURLConnection) u.openConnection();
        //h.setRequestMethod("GET");
        //return h.getResponseCode() != 404;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response
                = restTemplate.getForEntity(url, String.class);
        return response.getStatusCode().value() != 404;

      } catch (Exception e) {
        return false;
      }
    }
  }
}
