package urlshortener.team.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class ShortUrlServiceTest {
    @MockBean
    ShortURLRepository shortURLRepository;

    @Autowired
    ShortUrlService shortUrlService;

    public static final String TARGET = "https://google.com";
    public static final String SPONSOR = "https://wikipedia.com";
    public static final String IP = "localhost";

    @Test
    public void thatServiceSavesShortUrlWithSponsorAndTruePeriodicityAndTrueQr() {
        configureTransparentSave();
        ShortURL s = shortUrlService.createAndSaveShortUrl(TARGET, null, SPONSOR, IP, true, true);
        assertEquals(TARGET, s.getTarget());
        assertEquals(SPONSOR, s.getSponsor());
        assertEquals(true, s.isAliveOnLastCheck());
        assertEquals(true, s.isCheckStatus());
    }

    @Test
    public void thatServiceSavesShortUrlWithoutSponsorAndFalsePeriodicityAndFalseQr() {
        configureTransparentSave();
        ShortURL s = shortUrlService.createAndSaveShortUrl(TARGET, null, null, IP, false, false);
        assertEquals(TARGET, s.getTarget());
        assertEquals(null, s.getSponsor());
        assertEquals(false, s.isAliveOnLastCheck());
        assertEquals(false, s.isCheckStatus());
    }

    private void configureTransparentSave() {
        when(shortURLRepository.save(any(ShortURL.class)))
                .then((Answer<ShortURL>) invocation -> (ShortURL) invocation.getArguments()[0]);
    }
}
