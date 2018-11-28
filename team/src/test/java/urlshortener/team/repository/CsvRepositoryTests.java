package urlshortener.team.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.web.multipart.MultipartFile;
import urlshortener.team.domain.Click;
import urlshortener.team.domain.CsvFormat;
import urlshortener.team.repository.fixture.ClickFixture;
import urlshortener.team.repository.fixture.CsvRepositoryFixture;
import urlshortener.team.repository.fixture.ShortURLFixture;

import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

public class CsvRepositoryTests {

	private CsvRepository repository;


    @Before
    public void setup() {
        repository = new CsvRepositoryImpl();
    }

	@Test
    public void thatParseTheFileProperly(){
        MultipartFile correctCsv = CsvRepositoryFixture.getCorrectCsv();
        List<String> result = repository.parserCsv(correctCsv);
        assertEquals(result.size(), 3);
        assertEquals(result.get(2), "http://www.uri3.com");
    }

    @Test
    public void thatParseAFileIncorrectReturnNull(){

    }

    @Test
    public void thatShortsUrisProperly(){
        List<String> result = repository.shortUris(CsvRepositoryFixture.urisToShort());
        assertEquals(result.size(), 3);
    }

    @Test
    public void thatGeneratesTheColumnsOfACsv(){
        List<String> urisToShort = CsvRepositoryFixture.urisToShort();
        List<String> urisShorted = CsvRepositoryFixture.urisShorted();
        List<CsvFormat> result = repository.createCsv(urisToShort, urisShorted);
        assertEquals(result.get(2).getURIOriginal(), CsvRepositoryFixture.urisToShort().get(2));
        assertEquals(result.get(2).getURIAcortada(), CsvRepositoryFixture.urisShorted().get(2));
    }


}
