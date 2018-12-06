package urlshortener.team.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import urlshortener.team.config.PersistenceContext;
import urlshortener.team.domain.Job;
import urlshortener.team.repository.fixture.JobRepositoryFixture;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@Import(PersistenceContext.class)
@JdbcTest
public class JobRepositoryTests {

  @Autowired
  private JobRepository repository;

  @Autowired
  private JdbcTemplate jdbc;

  @Before
  public void setup() { }

  @Test
  public void thatFindByKeyReturnsAJob() {
    repository.save(JobRepositoryFixture.jobExample());
    Job j = repository.findByKey(JobRepositoryFixture.jobExample().getHash());
    assertNotNull(j);
    assertSame(j.getHash(), JobRepositoryFixture.jobExample().getHash());
  }

  @Test
  public void thatFindByKeyReturnsNullWhenFails() {
    assertNull(repository.findByKey(JobRepositoryFixture.jobExample().getHash()));
  }


  @Test
  public void thatSavePersistsAndUpdateTheJob() {
    Job j = repository.save(JobRepositoryFixture.jobExample());
    assertSame(jdbc.queryForObject("select count(*) from JOB",
            Integer.class), 1);
    assertNotNull(j);
    assertEquals(j.getHash(), "0");

    j.setHash("1");
    repository.update(j);
      assertNotNull(j);
      assertEquals(j.getHash(), "1");
  }


}
