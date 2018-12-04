package urlshortener.team.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.fixture.ShortURLFixture;

import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

public class ShortURLRepositoryTests {

  private EmbeddedDatabase db;
  private ShortURLRepository repository;
  private JdbcTemplate jdbc;

  @Before
  public void setup() {
    db = new EmbeddedDatabaseBuilder().setType(HSQL)
            .addScript("schema-hsqldb.sql").build();
    jdbc = new JdbcTemplate(db);
    repository = new ShortURLRepositoryImpl(jdbc);
  }

  @Test
  public void thatSavePersistsTheShortURL() {
    assertNotNull(repository.save(ShortURLFixture.url1()));
    assertSame(jdbc.queryForObject("select count(*) from SHORTURL",
            Integer.class), 1);
  }

  @Test
  public void thatSaveSponsor() {
    assertNotNull(repository.save(ShortURLFixture.urlSponsor()));
    assertSame(jdbc.queryForObject("select sponsor from SHORTURL",
            String.class), ShortURLFixture.urlSponsor().getSponsor());
  }

  @Test
  public void thatSaveSafe() {
    assertNotNull(repository.save(ShortURLFixture.urlSafe()));
    assertSame(
            jdbc.queryForObject("select safe from SHORTURL", Boolean.class),
            true);
    repository.mark(ShortURLFixture.urlSafe(), false);
    assertSame(
            jdbc.queryForObject("select safe from SHORTURL", Boolean.class),
            false);
    repository.mark(ShortURLFixture.urlSafe(), true);
    assertSame(
            jdbc.queryForObject("select safe from SHORTURL", Boolean.class),
            true);
  }

  @Test
  public void thatSaveADuplicateHashIsSafelyIgnored() {
    repository.save(ShortURLFixture.url1());
    assertNotNull(repository.save(ShortURLFixture.url1()));
    assertSame(jdbc.queryForObject("select count(*) from SHORTURL",
            Integer.class), 1);
  }

  @Test
  public void thatErrorsInSaveReturnsNull() {
    assertNull(repository.save(ShortURLFixture.badUrl()));
    assertSame(jdbc.queryForObject("select count(*) from SHORTURL",
            Integer.class), 0);
  }

  @Test
  public void thatFindByKeyReturnsAURL() {
    repository.save(ShortURLFixture.url1());
    repository.save(ShortURLFixture.url2());
    ShortURL su = repository.findByKey(ShortURLFixture.url1().getHash());
    assertNotNull(su);
    assertSame(su.getHash(), ShortURLFixture.url1().getHash());
  }

  @Test
  public void thatFindByKeyReturnsNullWhenFails() {
    repository.save(ShortURLFixture.url1());
    assertNull(repository.findByKey(ShortURLFixture.url2().getHash()));
  }

  @Test
  public void thatFindByTargetReturnsURLs() {
    repository.save(ShortURLFixture.url1());
    repository.save(ShortURLFixture.url2());
    repository.save(ShortURLFixture.url3());
    List<ShortURL> sul = repository.findByTarget(ShortURLFixture.url1().getTarget());
    assertEquals(sul.size(), 2);
    sul = repository.findByTarget(ShortURLFixture.url3().getTarget());
    assertEquals(sul.size(), 1);
    sul = repository.findByTarget("dummy");
    assertEquals(sul.size(), 0);
  }

  @Test
  public void thatDeleteDelete() {
    repository.save(ShortURLFixture.url1());
    repository.save(ShortURLFixture.url2());
    repository.delete(ShortURLFixture.url1().getHash());
    assertEquals(repository.count().intValue(), 1);
    repository.delete(ShortURLFixture.url2().getHash());
    assertEquals(repository.count().intValue(), 0);
  }

  @Test
  public void thatUpdateUpdate() {
    repository.save(ShortURLFixture.url1());
    ShortURL su = repository.findByKey(ShortURLFixture.url1().getHash());
    assertEquals(su.getTarget(), "http://www.unizar.es/");
    repository.update(ShortURLFixture.url1modified());
    su = repository.findByKey(ShortURLFixture.url1().getHash());
    assertEquals(su.getTarget(), "http://www.unizar.org/");
  }

  @Test
  public void thatGetAllToCheckReturnsOnlyRedirectsWithCheckStatus() {
    for (ShortURL i : ShortURLFixture.someURLs()) {
      repository.save(i);
    }
    for (ShortURL i : repository.getAllToCheck()) {
      assertEquals(i.isCheckStatus(), true);
    }
  }

  @After
  public void shutdown() {
    db.shutdown();
  }

}
