package urlshortener.team.repository;


import urlshortener.team.domain.Job;

public interface JobRepository{
  Job findByKey(String id);

  Job save(Job jl);

  void update(Job jl);
}
