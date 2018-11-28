package urlshortener.team.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import urlshortener.team.domain.Click;
import urlshortener.team.domain.Job;
import urlshortener.team.repository.fixture.ClickFixture;
import urlshortener.team.repository.fixture.CsvRepositoryFixture;
import urlshortener.team.repository.fixture.JobRepositoryFixture;
import urlshortener.team.repository.fixture.ShortURLFixture;

import static org.junit.Assert.*;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

public class JobRepositoryTests {

	private EmbeddedDatabase db;
	private JobRepository repository;
	private JdbcTemplate jdbc;

	@Before
	public void setup() {
		db = new EmbeddedDatabaseBuilder().setType(HSQL)
				.addScript("schema-hsqldb.sql").build();
		jdbc = new JdbcTemplate(db);
		repository = new JobRepositoryImpl(jdbc);
	}

	@Test
	public void thatFindByKeyReturnsAJob() {
		repository.save(JobRepositoryFixture.jobExample());
		Job j = repository.findByKey(JobRepositoryFixture.jobExample().getHash());
		assertNotNull(j);
		assertSame(j.getHash(), JobRepositoryFixture.jobExample().getHash());
	}

	@Test
	public void thatFindByKeyReturnsNullWhenFails() {
		repository.save(JobRepositoryFixture.jobExample());
		assertNull(repository.findByKey(JobRepositoryFixture.jobExample().getHash()));
	}


    @Test
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
        try{
            Thread.sleep(20000);
            j = repository.findByKey(JobRepositoryFixture.jobWithUris().getHash());
            assertNotNull(j.getResult());
            assertEquals(j.getResult().size(), 3);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }


	@After
	public void shutdown() {
		db.shutdown();
	}

}
