package urlshortener.team.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class UriServiceTest {
    @Autowired
    UriService uriService;

    @Test
    public void thatBadSyntaxReturnsFalse() {
        assertEquals(uriService.checkSyntax("bad syntax"), false);
    }

    @Test
    public void thatGoodSyntaxReturnsTrue() {
        assertEquals(uriService.checkSyntax("http://paginainexistenteejemplo.es"), true);
        assertEquals(uriService.checkSyntax("https://paginainexistenteejemplo.es"), true);
    }

    @Test
    public void thatUnreachableUriReturnsFalse() {
        assertEquals(uriService.checkAlive("http://paginainexistenteejemplo.es"), false);
        assertEquals(uriService.checkAlive("https://google.com/noexisto.html"), false);
    }

    @Test
    public void thatReachableUriReturnsTrue() {
        assertEquals(uriService.checkAlive("http://es.simplesite.com/"), true);
        assertEquals(uriService.checkAlive("https://marca.com"), true);
    }
}
