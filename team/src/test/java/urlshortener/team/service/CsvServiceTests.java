package urlshortener.team.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;
import urlshortener.team.domain.CsvFormat;
import urlshortener.team.repository.fixture.CsvRepositoryFixture;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CsvServiceTests {

  private CsvService repository;


  @Before
  public void setup() {
    repository = new CsvServiceImpl();
  }

  @Test
  public void thatParseTheFileProperly() {
    MultipartFile correctCsv = CsvRepositoryFixture.getCorrectCsv();
    List<String> result = repository.parserCsv(correctCsv);
    assertEquals(result.size(), 3);
    assertEquals(result.get(2), "http://www.uri3.com");
  }

  @Test
  public void thatParseAFileIncorrectReturnNull() {

  }

  @Test
  public void thatShortsUrisProperly() {
    List<String> result = repository.shortUris(CsvRepositoryFixture.urisToShort());
    assertEquals(result.size(), 3);
  }

  @Test
  public void thatGeneratesTheColumnsOfACsv() {
    List<String> urisToShort = CsvRepositoryFixture.urisToShort();
    List<String> urisShorted = CsvRepositoryFixture.urisShorted();
    List<CsvFormat> result = repository.createCsv(urisToShort, urisShorted);
    assertEquals(result.get(2).getURIOriginal(), CsvRepositoryFixture.urisToShort().get(2));
    assertEquals(result.get(2).getURIAcortada(), CsvRepositoryFixture.urisShorted().get(2));
  }


}
