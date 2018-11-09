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
import urlshortener.team.domain.Click;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.ShortURLRepository;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class SponsorController {
	private static final Logger LOG = LoggerFactory
			.getLogger(SponsorController.class);
	@Autowired
	protected ShortURLRepository shortURLRepository;

	@Autowired
	protected ClickRepository clickRepository;

	@RequestMapping(value = "/sponsor/{id:(?!link).*}", method = RequestMethod.GET)
	public ResponseEntity<String> redirectToSponsor(@PathVariable String id,
			HttpServletRequest request) {

	    ShortURL shorted = shortURLRepository.findByKey(id);
	    String sponsor = shorted.getSponsor();
		if (shorted != null) {
			HttpHeaders h = new HttpHeaders();
			h.setLocation(shorted.getUri());
			return new ResponseEntity<>(sponsor, h, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
