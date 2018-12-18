package urlshortener.team.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.team.domain.ApiResponse;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;

import javax.servlet.http.HttpServletResponse;

@RestController
public class QRController {

  private static final Logger LOG = LoggerFactory
          .getLogger(QRController.class);

  @Autowired
  protected ShortURLRepository shortURLRepository;

  @GetMapping("/{id}/qr")
  public ResponseEntity<?> qr(@PathVariable String id,HttpServletResponse response) {
      ShortURL s = shortURLRepository.findByKey(id);
      if (s == null) {
          return new ResponseEntity("ERROR: " + id + " does not reference any url",
                  HttpStatus.BAD_REQUEST);
      }
      if (!s.getQR()) {
          return new ResponseEntity<>("ERROR: QR wasn't asked when you shorted your url",
                  HttpStatus.NOT_FOUND);
          //throw new NotFoundException(id + "doesn't have qr associated because it wasn't \n"+
            //      "asked when it was shorted");
      }
      if (s.getQRimage() == null) {
          HttpHeaders h = new HttpHeaders();
          h.set(HttpHeaders.RETRY_AFTER,"5 secs");
          return new ResponseEntity<>("ERROR: Your QR is being created",
                  h,HttpStatus.NOT_FOUND);
          //throw new NotFoundTimeoutException(id + " qr it's being created in this moment");
      }
      return new ResponseEntity<>(s.getQRimage(), HttpStatus.OK);
  }
}