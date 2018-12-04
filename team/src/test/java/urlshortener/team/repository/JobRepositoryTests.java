package urlshortener.team.repository;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import urlshortener.team.config.PersistenceContext;
import urlshortener.team.domain.Job;
import urlshortener.team.repository.fixture.CsvRepositoryFixture;
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
  public void setup() {
  }

  @Test
  public void thatFindByKeyReturnsAJob() {
    repository.save(JobRepositoryFixture.jobExample());
    Job j = repository.findByKey(JobRepositoryFixture.jobExample().getHash());
    assertNotNull(j);
    assertSame(j.getHash(), JobRepositoryFixture.jobExample().getHash());
  }

  @Test
  @Ignore
  public void thatFindByKeyReturnsNullWhenFails() {
    repository.save(JobRepositoryFixture.jobExample());
    assertNull(repository.findByKey(JobRepositoryFixture.jobExample().getHash()));
  }


  @Test
  @Ignore
  public void thatSavePersistsTheJob() {
    Job j = repository.save(JobRepositoryFixture.jobExample());
    assertSame(jdbc.queryForObject("select count(*) from JOB",
            Integer.class), 1);
    assertNotNull(j);
    assertEquals(j.getHash(), "0");
  }

  @Test
  public void thatProcessTheJobProperly() {
    Job j = repository.save(JobRepositoryFixture.jobWithUris());
    repository.processJob(j, CsvRepositoryFixture.urisToShort());
    try {
      Thread.sleep(20000);
      j = repository.findByKey(JobRepositoryFixture.jobWithUris().getHash());
      assertNotNull(j.getResult());
      assertEquals(j.getResult().size(), 3);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


}
