package urlshortener.team.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.team.service.fixture.ShortUrlFixture;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class AliveServiceTest {
    @Autowired
    ShortURLRepository shortURLRepository;

    @Autowired
    AliveService aliveService;

    @Test
    public void thatTaskModifysReachableTargetWithFalseStatus() {
        shortURLRepository.save(ShortUrlFixture.reachableAndLastCheckFalse());
        aliveService.task();
        ShortURL s = shortURLRepository.findByKey("1");
        assertEquals(s.isAliveOnLastCheck(), true);
    }

    @Test
    public void thatTaskDoesNotModifyReachableTargetWithTrueStatus() {
        shortURLRepository.save(ShortUrlFixture.reachableAndLastCheckTrue());
        aliveService.task();
        ShortURL s = shortURLRepository.findByKey("2");
        assertEquals(s.isAliveOnLastCheck(), true);
    }

    @Test
    public void thatTaskModifysUnreachableTargetWithTrueStatus() {
        shortURLRepository.save(ShortUrlFixture.unreachableAndLastCheckTrue());
        aliveService.task();
        ShortURL s = shortURLRepository.findByKey("3");
        assertEquals(s.isAliveOnLastCheck(), false);
    }

    @Test
    public void thatTaskDoesNotModifyUnreachableTargetWithFalseStatus() {
        shortURLRepository.save(ShortUrlFixture.unreachableAndLastCheckFalse());
        aliveService.task();
        ShortURL s = shortURLRepository.findByKey("4");
        assertEquals(s.isAliveOnLastCheck(), false);
    }
}
