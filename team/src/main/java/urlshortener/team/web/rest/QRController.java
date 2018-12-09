package urlshortener.team.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class QRController {
  private static final Logger LOG = LoggerFactory
          .getLogger(QRController.class);
  @Autowired
  protected ShortURLRepository shortURLRepository;

  @RequestMapping(value = "/{id:(?!link).*}/qr", method = RequestMethod.GET)
  public ResponseEntity qr(@PathVariable String id,
                           HttpServletResponse response) throws IOException {
    ShortURL s = shortURLRepository.findByKey(id);
    if (s != null) {
      if (s.getQR()) {
        if (s.getQRimage() == null) {
          HttpHeaders h = new HttpHeaders();
          h.set(HttpHeaders.RETRY_AFTER, "5");
          return new ResponseEntity<>(h, HttpStatus.NOT_FOUND);
        } else {
          HttpHeaders h = new HttpHeaders();
          h.setContentType(MediaType.IMAGE_PNG);
          return new ResponseEntity<>(s.getQRimage(), h, HttpStatus.OK);
        }
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }


}
