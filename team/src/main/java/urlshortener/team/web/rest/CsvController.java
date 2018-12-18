package urlshortener.team.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import urlshortener.team.domain.CsvFormat;
import urlshortener.team.domain.Job;
import urlshortener.team.repository.JobRepository;
import urlshortener.team.service.JobService;
import urlshortener.team.service.JobServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class CsvController {

    private static int id = 0;

    @Autowired
    protected JobService jobService;

    @Autowired
    protected JobRepository jobRepository;


    @RequestMapping(value = "/uploadCSV", method = RequestMethod.POST)
    public ResponseEntity<String> downloadCSV(@RequestParam("file") MultipartFile file,
                                              HttpServletRequest request) {

        URI uri = linkTo(methodOn(CsvController.class).downloadCSV(null, null)).toUri();
        URI uriBase = URI.create(uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort());

        List<String> uris = jobService.parserCsv(file);
        if (uris == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Fichero CSV mal formado");
        } else {
            // It creates a new async job
            int idInt = id++; // job id
            String id = Integer.toString(idInt);
            Job job = new Job(id, 0, uris.size(), null, null);
            jobRepository.save(job);
            jobService.processJob(job, uris, request.getRemoteAddr(), uriBase); // is async

            URI uriLocation = URI.create(uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort() + "/job/" + job.getHash());
            HttpHeaders h = new HttpHeaders();
            h.setLocation(uriLocation);
            return new ResponseEntity<>(h, HttpStatus.ACCEPTED);
        }
    }

    @RequestMapping(value = "/job/{id:(?!link).*}", method = RequestMethod.GET)
    public ResponseEntity<Job> job(@PathVariable String id) {
        Job j = jobRepository.findByKey(id);
        if (j != null) {
            HttpHeaders h = new HttpHeaders();
            h.set(HttpHeaders.RETRY_AFTER, "1"); // in seconds
            j.setResult(null); // In order to not send all the result, you must to call the result method
            return new ResponseEntity<>(j, h, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/result/{id:(?!link).*}", method = RequestMethod.GET)
    public ResponseEntity<String> result(@PathVariable String id,
                                         HttpServletResponse response) {
        Job j = jobRepository.findByKey(id);
        if (j != null && j.getResult() != null) {

            jobService.generateCsvResponse(j, response);
            response.setContentType("text/csv");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}