package urlshortener.team.service;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.service.fixture.ShortUrlFixture;

import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SponsorServiceTests {
    @Autowired
    protected SponsorService sponsorService;

    @Before
    public void setup() {

    }

    @Test
    public void thatGenerateHtmlIncludesValidSponsor(){
        ShortURL shortURL = ShortUrlFixture.reachableAndValidSponsor();
        String html = sponsorService.generateHtml(shortURL);

        assertThat(html,CoreMatchers.containsString("<iframe src=\""+shortURL.getSponsor()+"\""));
    }

    @Test
    public void thatGenerateHtmlIncludesDefaultSponsor(){
        ShortURL shortURL = ShortUrlFixture.reachableAndInvalidSponsor();
        String html = sponsorService.generateHtml(shortURL);

        assertThat(html,CoreMatchers.containsString("<iframe src=\""+sponsorService.getDefaultSponsorUri()+"\""));
    }
}
