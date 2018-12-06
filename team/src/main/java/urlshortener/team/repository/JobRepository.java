package urlshortener.team.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import urlshortener.team.domain.Job;

public interface JobRepository{
  Job findByKey(String id);

  Job save(Job jl);

  void update(Job jl);
}
