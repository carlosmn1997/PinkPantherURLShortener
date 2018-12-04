package urlshortener.team.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import urlshortener.team.domain.ApiResponse;
import urlshortener.team.domain.Click;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.domain.ValidUrl;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.QRRepository;
import urlshortener.team.repository.ShortURLRepository;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.sql.Date;

@Controller
public class UrlShortenerController {
  private static final Logger LOG = LoggerFactory
          .getLogger(UrlShortenerController.class);
  @Autowired
  protected ShortURLRepository shortURLRepository;

  @Autowired
  protected ClickRepository clickRepository;

  @Autowired
  protected QRRepository qrRepository;

  @RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
  public ResponseEntity<?> redirectTo(@PathVariable String id,
                                      HttpServletRequest request) {
    ShortURL l = shortURLRepository.findByKey(id);
    if (l == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if (l.getSponsor() != null) {
      return ResponseEntity.ok(SponsorController.generateHtml(l));
    }
    createAndSaveClick(id, extractIP(request));
    return createSuccessfulRedirectToResponse(l);
  }

  @RequestMapping(value = "/short", method = RequestMethod.POST)
  public ResponseEntity<?> shortener(@RequestParam("uri") String uri,
                                     @RequestParam(value = "periodicity") boolean periodicity,
                                     @RequestParam(value = "qr") boolean qr,
                                     @RequestParam(value = "sponsor", required = false) String sponsor,
                                     HttpServletRequest request) {
    ValidUrl url = new ValidUrl(uri);
    if (!url.checkSyntax()) {
      throw new BadRequestException("Bad syntax");
    }
    ShortURL su = new ShortURL(uri, sponsor, extractIP(request), periodicity, qr);
    su = shortURLRepository.save(su);
    if (su == null) {
      throw new BadRequestException("Cannot save the url");
    }
    if (qr) {
      qrRepository.createQR(su.getHash(), su.getUri().toString());
    }
    HttpHeaders h = new HttpHeaders();
    h.setLocation(su.getUri());
    return new ResponseEntity<>(su, h, HttpStatus.CREATED);
  }

  class BadRequestException extends RuntimeException {

    public BadRequestException(String msg) {
      super(msg);
    }
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(BadRequestException.class)
  @ResponseBody
  ApiResponse handleBadRequest(BadRequestException ex) {
    return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "INV", ex.getMessage());
  }

  @RequestMapping(value = "/{id}/alive", method = RequestMethod.GET)
  public ResponseEntity<?> isAlive(@PathVariable String id) {
    ShortURL su = shortURLRepository.findByKey(id);
    if (su != null) { // id exists
      if (su.isCheckStatus()) { // check was enabled
        return new ResponseEntity<>(su.isAliveOnLastCheck(), HttpStatus.OK);
      } else { // check was not enabled
        ApiResponse a = new ApiResponse(HttpStatus.NOT_FOUND.value(), "INV", "Check status is false for " + id);
        return new ResponseEntity<>(a, HttpStatus.NOT_FOUND);
      }
    } else { // id does not exist
      ApiResponse a = new ApiResponse(HttpStatus.NOT_FOUND.value(), "INV", id + " does not reference any url");
      return new ResponseEntity<>(a, HttpStatus.NOT_FOUND);
    }
  }

  /*
   * PRIVATE METHODS
   */
  private void createAndSaveClick(String hash, String ip) {
    Click cl = new Click(null, hash, new Date(System.currentTimeMillis()),
            null, null, null, ip, null);
    cl = clickRepository.save(cl);
    LOG.info(cl != null ? "[" + hash + "] saved with id [" + cl.getId() + "]" : "[" + hash + "] was not saved");
  }

  private String extractIP(HttpServletRequest request) {
    return request.getRemoteAddr();
  }

  private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
    HttpHeaders h = new HttpHeaders();
    h.setLocation(URI.create(l.getTarget()));
    return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
  }
}
