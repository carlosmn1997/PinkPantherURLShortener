package urlshortener.team.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;
import urlshortener.team.domain.CsvFormat;

import java.util.List;

public interface CsvService {

  List<String> parserCsv(MultipartFile file);

  @Async
  List<String> shortUris(List<String> urisToShort);

  // Return the location of the file
  List<CsvFormat> createCsv(List<String> original, List<String> formatted);
}
