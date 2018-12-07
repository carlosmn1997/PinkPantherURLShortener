package urlshortener.team.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;
import urlshortener.team.config.PersistenceContext;
import urlshortener.team.config.ServiceContext;
import urlshortener.team.domain.CsvFormat;
import urlshortener.team.domain.Job;
import urlshortener.team.repository.*;
import urlshortener.team.repository.fixture.ShortURLFixture;
import urlshortener.team.service.fixture.CsvRepositoryFixture;
import urlshortener.team.repository.fixture.JobRepositoryFixture;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JobServiceTests {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobService jobService;


    @Before
    public void setup() {

    }

  @Test
  public void thatParseTheFileProperly() {
    MultipartFile correctCsv = CsvRepositoryFixture.getCorrectCsv();
    List<String> result = jobService.parserCsv(correctCsv);
    assertEquals(result.size(), 3);
    assertEquals(result.get(2), "http://www.uri3.com");
  }

  @Test
  public void thatParseAFileIncorrectReturnNull() {
      MultipartFile incorrectCsv = CsvRepositoryFixture.getIncorrectCsv();
      List<String> result = jobService.parserCsv(incorrectCsv);
      assertNull(result);
  }

  @Test
  public void thatGeneratesTheColumnsOfACsv() {
    List<String> urisToShort = CsvRepositoryFixture.urisToShort();
    List<String> urisShorted = CsvRepositoryFixture.urisShorted();
    List<CsvFormat> result = jobService.createCsv(urisToShort, urisShorted);
    assertEquals(result.get(2).getURIOriginal(), CsvRepositoryFixture.urisToShort().get(2));
    assertEquals(result.get(2).getURIAcortada(), CsvRepositoryFixture.urisShorted().get(2));
  }

  @Test
  public void thatProcessTheJobProperly() {
    Job j = jobRepository.save(JobRepositoryFixture.jobWithUris());
    jobService.processJob(j, CsvRepositoryFixture.urisToShort());
    try {
      Thread.sleep(5000);
      j = jobRepository.findByKey(JobRepositoryFixture.jobWithUris().getHash());
      assertNotNull(j.getResult());
      assertEquals(j.getResult().size(), 3);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
