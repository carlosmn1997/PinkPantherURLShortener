package urlshortener.team.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.domain.URLChecker;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.sql.Date;

@RestController
public class ShortAndCheckController {

    URLChecker checker;

    @RequestMapping(value = "/shortAndCheck", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> shortAndCheck(@RequestParam("uri") String uri,
                                              @RequestParam(value = "sponsor", required = false) String sponsor,
                                              HttpServletRequest request)  throws Exception {
        // Comprobamos si la uri es correcta
        if(checker.checkUri(uri)) {
            // Aquí habría que redirigir a /short
            ShortURL su = new ShortURL("1", uri, new URI("www.prueba.com"), "A", new Date(1), "A", 1, true, "ip", "Spain");
            return new ResponseEntity<>(su, new HttpHeaders(), HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }
}
