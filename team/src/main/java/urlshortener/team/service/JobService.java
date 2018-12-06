package urlshortener.team.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;
import urlshortener.team.domain.CsvFormat;
import urlshortener.team.domain.Job;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface JobService {

    List<String> parserCsv(MultipartFile file);


  @Async
  void processJob(Job job, List<String> urisToShort);

  List<String> shortUris(List<String> urisToShort, Job job);

  // Return the location of the file
  List<CsvFormat> createCsv(List<String> original, List<String> formatted);

  void generateCsvResponse(Job j, HttpServletResponse response);


}
