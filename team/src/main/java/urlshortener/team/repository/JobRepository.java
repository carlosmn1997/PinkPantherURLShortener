package urlshortener.team.repository;

import org.springframework.scheduling.annotation.Async;
import urlshortener.team.domain.CsvFormat;
import urlshortener.team.domain.Job;

import java.util.List;

public interface JobRepository {

  Job findByKey(String id);

  Job save(Job jl);

  void update(Job jl);

  @Async
  void processJob(Job job, List<String> urisToShort);

  List<String> shortUris(List<String> urisToShort, Job job);

  // Return the location of the file
  List<CsvFormat> createCsv(List<String> original, List<String> formatted);

}
