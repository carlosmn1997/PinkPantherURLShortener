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
import org.springframework.web.multipart.MultipartFile;
import urlshortener.team.domain.Click;
import urlshortener.team.domain.CsvFormat;
import urlshortener.team.domain.Job;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.CsvRepository;
import urlshortener.team.repository.JobRepository;
import urlshortener.team.repository.ShortURLRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

@RestController
public class CsvController {

	@Autowired
	protected CsvRepository csvRepository;


	@Autowired
    protected JobRepository jobRepository;


    @RequestMapping(value = "/uploadCSV", method = RequestMethod.GET)
    public ResponseEntity<String> downloadCSV(HttpServletResponse response, //@RequestParam("file") MultipartFile file,
                            HttpServletRequest request) throws IOException {
        MultipartFile file = null;

        String csvFileName = "mock.csv";

        response.setContentType("text/csv");

        // creates mock data
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                csvFileName);
        response.setHeader(headerKey, headerValue);

        List<String> uris = csvRepository.parserCsv(file);
        if(uris == null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Fichero CSV mal formado");
        }
        else{

            // I create a new job
            // Must be async
            List<String> urisShorted = csvRepository.shortUris(uris);

            // when it has finished, create CSV
            List<CsvFormat> csvList = csvRepository.createCsv(uris, urisShorted);

            Job job = new Job("123", 0, 125, null, csvList);
            System.out.println(job.getResult().get(0).toString());
            jobRepository.save(job);
            Job job2 = jobRepository.findByKey(job.getHash());

            // Finish writing CSV
            // Store csvList as a BLOB

            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/job/{id:(?!link).*}", method = RequestMethod.GET)
    public ResponseEntity<Job> job(@PathVariable String id,
                                        HttpServletRequest request) {
        Job j = jobRepository.findByKey(id);
        if(j != null){
            HttpHeaders h = new HttpHeaders();
            h.setLocation(j.getUriResult());
            return new ResponseEntity<>(j, h, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/result/{id:(?!link).*}", method = RequestMethod.GET)
    public ResponseEntity<String> job(@PathVariable String id,
                                   HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        Job j = jobRepository.findByKey(id);
        if(j != null){
            if(j.getConverted() == j.getTotal()){
                List<CsvFormat> csvList = j.getResult();
                // uses the Super CSV API to generate CSV data from the model data
                ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                        CsvPreference.STANDARD_PREFERENCE);

                String[] header = {
                        "URIOriginal",
                        "URIAcortada"
                };

                csvWriter.writeHeader(header);

                for (CsvFormat aFile: csvList) {
                    csvWriter.write(aFile, header);
                }

                csvWriter.close();
                return new ResponseEntity<>(HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}