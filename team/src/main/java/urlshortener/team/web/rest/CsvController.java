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
import urlshortener.team.service.CsvService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class CsvController {

  private static int id = 0;

  @Autowired
  protected CsvService csvRepository;


  @Autowired
  protected JobRepository jobRepository;


  @RequestMapping(value = "/uploadCSV", method = RequestMethod.POST)
  public ResponseEntity<String> downloadCSV(@RequestParam("file") MultipartFile file) throws IOException {

    List<String> uris = csvRepository.parserCsv(file);
    if (uris == null) {
      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body("Fichero CSV mal formado");
    } else {
      // It creates a new job
      // Must be async
      int idInt = this.id++; // job id
      String id = Integer.toString(idInt);
      Job job = new Job(id, 0, uris.size(), null, null);
      jobRepository.save(job);
      jobRepository.processJob(job, uris); // is async


      //System.out.println(job.getResult().get(0).toString());
      //jobRepository.save(job);
      //Job job2 = jobRepository.findByKey(job.getHash());

      // Finish writing CSV
      // Store csvList as a BLOB

      return new ResponseEntity<>("http://localhost:8080/job/" + id, HttpStatus.CREATED);
    }
  }

  @RequestMapping(value = "/job/{id:(?!link).*}", method = RequestMethod.GET)
  public ResponseEntity<Job> job(@PathVariable String id,
                                 HttpServletResponse response) throws IOException {
    Job j = jobRepository.findByKey(id);
    if (j != null) {
      HttpHeaders h = new HttpHeaders();
      h.setLocation(j.getUriResult());
      return new ResponseEntity<>(j, h, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/result/{id:(?!link).*}", method = RequestMethod.GET)
  public ResponseEntity<String> result(@PathVariable String id,
                                       HttpServletResponse response) throws IOException {
    Job j = jobRepository.findByKey(id);
    if (j != null && j.getResult() != null) {
      String csvFileName = "mock.csv";

      response.setContentType("text/csv");

      // creates mock data
      String headerKey = "Content-Disposition";
      String headerValue = String.format("attachment; filename=\"%s\"",
              csvFileName);
      response.setHeader(headerKey, headerValue);

      List<CsvFormat> csvList = j.getResult();
      // uses the Super CSV API to generate CSV data from the model data
      ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
              CsvPreference.STANDARD_PREFERENCE);

      String[] header = {
              "URIOriginal",
              "URIAcortada"
      };

      csvWriter.writeHeader(header);

      for (CsvFormat aFile : csvList) {
        csvWriter.write(aFile, header);
      }

      csvWriter.close();
      return new ResponseEntity<>(HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }
}