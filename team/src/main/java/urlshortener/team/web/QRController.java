package urlshortener.team.web;

import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import urlshortener.team.domain.Click;
import urlshortener.team.domain.Job;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.ShortURLRepository;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class QRController {
    private static final Logger LOG = LoggerFactory
            .getLogger(SponsorController.class);
    @Autowired
    protected ShortURLRepository shortURLRepository;

    @RequestMapping(value = "/{id:(?!link).*}/qr", method = RequestMethod.GET)
    public ResponseEntity qr(@PathVariable String id,
                                   HttpServletResponse response) throws IOException {
        ShortURL s = shortURLRepository.findByKey(id);
        if(s != null){
            if(s.getQR()) {
                if (s.getQRimage() == null) {
                    HttpHeaders h = new HttpHeaders();
                    h.set(HttpHeaders.RETRY_AFTER,"5");
                    return new ResponseEntity<>(h,HttpStatus.NOT_FOUND);
                } else {
                    HttpHeaders h = new HttpHeaders();
                    h.setContentType(MediaType.IMAGE_PNG);
                    return new ResponseEntity<>(s.getQRimage(), h,HttpStatus.OK);
                }
            }
            else{
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
