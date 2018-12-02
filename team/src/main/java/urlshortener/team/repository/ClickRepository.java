package urlshortener.team.repository;

import java.util.List;

import urlshortener.team.domain.Click;

public interface ClickRepository {

	List<Click> findByHash(String hash);

	Long clicksByHash(String hash);

	Click save(Click cl);

	void update(Click cl);

	void delete(Long id);

	void deleteAll();

	Long count();

	List<Click> list(Long limit, Long offset);

	void createAndSaveClick(String hash, String ip);
}
