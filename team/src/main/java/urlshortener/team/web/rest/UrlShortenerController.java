package urlshortener.team.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.team.domain.ApiResponse;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.service.ShortUrlService;
import urlshortener.team.service.SponsorService;
import urlshortener.team.service.UriService;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.QRRepository;
import urlshortener.team.repository.ShortURLRepository;
import javax.servlet.http.HttpServletRequest;

@RestController
public class UrlShortenerController {
  @Autowired
  protected ShortURLRepository shortURLRepository;

  @Autowired
  protected ClickRepository clickRepository;

  @Autowired
  protected QRRepository qrRepository;

  @Autowired
  protected ShortUrlService shortUrlService;

  @Autowired
  protected UriService uriService;

  @Autowired
  protected SponsorService sponsorService;

  @GetMapping("/{id:(?!link|index).*}")
  public ResponseEntity<?> redirectTo(@PathVariable String id,
                                      HttpServletRequest request) {
    ShortURL l = shortURLRepository.findByKey(id);
    if (l == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if (l.getSponsor() != null) {
      return ResponseEntity.ok(sponsorService.generateHtml(l));
    }
    shortUrlService.createAndSaveClick(id, request.getRemoteAddr());
    return shortUrlService.createSuccessfulRedirectToResponse(l);
  }

  @PostMapping("/short")
  public ResponseEntity<?> shortener(@RequestParam("uri") String uri,
                                     @RequestParam(value = "periodicity") boolean periodicity,
                                     @RequestParam(value = "qr") boolean qr,
                                     @RequestParam(value = "sponsor", required = false) String sponsor,
                                     HttpServletRequest request) {
    if (!uriService.checkSyntax(uri) ||
            sponsor!=null && !sponsor.isEmpty() && !uriService.checkSyntax(sponsor)) {
      throw new BadRequestException("Bad syntax");
    }
    ShortURL su = shortUrlService.createAndSaveShortUrl(uri, sponsor,
            request.getRemoteAddr(), periodicity, qr);
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

  @GetMapping("/{id}/alive")
  public ResponseEntity<?> isAlive(@PathVariable String id) {
    ShortURL su = shortURLRepository.findByKey(id);
    if(su == null) {
      throw new UrlShortenerController.NotFoundException(id + " does not reference any url");
    }
    if(!su.isCheckStatus()) {
      throw new UrlShortenerController.NotFoundException("Check status is false for " + id);
    }
    return new ResponseEntity<>(su.isAliveOnLastCheck(), HttpStatus.OK);
  }

   /***********************
   *  EXCEPTION HANDLERS  *
   ***********************/
  public class BadRequestException extends RuntimeException {

    public BadRequestException(String msg) {
      super(msg);
    }
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler(BadRequestException.class)
  public ApiResponse handleBadRequest(BadRequestException ex) {
    return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "error", ex.getMessage());
  }

  public class NotFoundException extends RuntimeException {

    public NotFoundException(String msg) {
      super(msg);
    }
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  @ExceptionHandler(NotFoundException.class)
  public ApiResponse handleNotFound(NotFoundException ex) {
    return new ApiResponse(HttpStatus.NOT_FOUND.value(), "error", ex.getMessage());
  }
}
