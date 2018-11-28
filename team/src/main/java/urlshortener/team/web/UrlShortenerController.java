package urlshortener.team.web;

import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.team.domain.Click;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import urlshortener.team.domain.ApiResponse;

@RestController
public class UrlShortenerController {
	private static final Logger LOG = LoggerFactory
			.getLogger(UrlShortenerController.class);
	@Autowired
	protected ShortURLRepository shortURLRepository;

	@Autowired
	protected ClickRepository clickRepository;

	@RequestMapping(value = "/{id:(?!link).*}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id,
			HttpServletRequest request) {
		ShortURL l = shortURLRepository.findByKey(id);
		if (l != null) {
			createAndSaveClick(id, extractIP(request));
			return createSuccessfulRedirectToResponse(l);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	private void createAndSaveClick(String hash, String ip) {
		Click cl = new Click(null, hash, new Date(System.currentTimeMillis()),
				null, null, null, ip, null);
		cl=clickRepository.save(cl);
		LOG.info(cl!=null?"["+hash+"] saved with id ["+cl.getId()+"]":"["+hash+"] was not saved");
	}

	private String extractIP(HttpServletRequest request) {
		return request.getRemoteAddr();
	}

	private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
		HttpHeaders h = new HttpHeaders();
		h.setLocation(URI.create(l.getTarget()));
		return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
	}

	@RequestMapping(value = "/short", method = RequestMethod.POST)
	public ResponseEntity<?> shortener(@RequestParam("uri") String uri,
                                       @RequestParam(value = "periodicity") boolean periodicity,
                                       @RequestParam(value = "qr") boolean qr,
                                       @RequestParam(value = "sponsor", required = false) String sponsor,
                                       HttpServletRequest request) {
		ShortURL su = createAndSaveIfValid(uri, sponsor, UUID
				.randomUUID().toString(), extractIP(request), periodicity, qr);
		if (su != null) {
		    if(qr) {
                // LLAMAR AQUÍ A MÉTODO DE CREAR QR
            }
			HttpHeaders h = new HttpHeaders();
			h.setLocation(su.getUri());
			return new ResponseEntity<>(su, h, HttpStatus.CREATED);
		} else {
			ApiResponse a = new ApiResponse(HttpStatus.BAD_REQUEST.value(), "INV",uri + " is not a valid url");
			return new ResponseEntity<>(a, HttpStatus.BAD_REQUEST);
		}
	}

	private ShortURL createAndSaveIfValid(String url, String sponsor,
										  String owner, String ip,
										  boolean periodicity, boolean qr) {
		UrlValidator urlValidator = new UrlValidator(new String[] { "http",
				"https" });
		if (urlValidator.isValid(url)) {
			String id = Hashing.murmur3_32()
					.hashString(url, StandardCharsets.UTF_8).toString();
			ShortURL su = new ShortURL(id, url,
					linkTo(
							methodOn(UrlShortenerController.class).redirectTo(
									id, null)).toUri(), sponsor, new Date(
					System.currentTimeMillis()), owner,
					HttpStatus.TEMPORARY_REDIRECT.value(), true, ip, null,
					periodicity, false);
			return shortURLRepository.save(su);
		} else {
			return null;
		}
	}

	@RequestMapping(value = "/{id}/alive", method = RequestMethod.GET)
    public ResponseEntity<?> isAlive(@PathVariable String id) {
        ShortURL su = shortURLRepository.findByKey(id);
        if(su != null) { // id exists
            if(su.isCheckStatus()) { // check was enabled
                return new ResponseEntity<>(su.isAliveOnLastCheck(), HttpStatus.OK);
            }
            else { // check was not enabled
                ApiResponse a = new ApiResponse(HttpStatus.NOT_FOUND.value(), "INV", "Check status is false for " + id);
                return new ResponseEntity<>(a, HttpStatus.NOT_FOUND);
            }
        }
        else { // id does not exist
            ApiResponse a = new ApiResponse(HttpStatus.NOT_FOUND.value(), "INV",id + " does not reference any url");
            return new ResponseEntity<>(a, HttpStatus.NOT_FOUND);
        }
    }
}
