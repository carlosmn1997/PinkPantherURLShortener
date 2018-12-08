package urlshortener.team.web.html;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.IOException;

@Controller
public class Csv {
    @Value("${:classpath:/static/csv.html}")
    private Resource index;

    @GetMapping(value = {"/csv.html"}, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public ResponseEntity actions() throws IOException {
        return ResponseEntity.ok(new InputStreamResource(index.getInputStream()));
    }
}
