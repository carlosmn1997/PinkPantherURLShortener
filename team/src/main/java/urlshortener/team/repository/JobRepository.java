package urlshortener.team.repository;

import urlshortener.team.domain.Click;
import urlshortener.team.domain.Job;
import urlshortener.team.domain.ShortURL;

import java.util.List;

public interface JobRepository {

	Job findByKey(String id);

	Job save(Job jl);

	void update(Job jl);

}
