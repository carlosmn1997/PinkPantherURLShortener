package urlshortener.team.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.domain.ValidUrl;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.team.web.fixture.ShortURLFixture;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UrlShortenerTests {

    private MockMvc mockMvc;

    @Mock
    private ClickRepository clickRepository;

    @Mock
    private ShortURLRepository shortURLRepository;

    @Mock
    private ValidUrl validUrl;

    @InjectMocks
    private UrlShortenerController urlShortener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build();
    }

    @Test
    public void thatRedirectToReturnsTemporaryRedirectIfKeyExists()
            throws Exception {
        when(shortURLRepository.findByKey("someKey")).thenReturn(ShortURLFixture.someUrl());
        when(clickRepository.save(any())).thenAnswer((InvocationOnMock invocation) -> invocation.getArguments()[0]);

        mockMvc.perform(get("/{id}", "someKey")).andDo(print())
                .andExpect(status().isTemporaryRedirect())
                .andExpect(redirectedUrl("http://example.com/"));
    }

    @Test
    public void thatRedirecToReturnsNotFoundIdIfKeyDoesNotExist()
            throws Exception {
        when(shortURLRepository.findByKey("someKey")).thenReturn(null);

        mockMvc.perform(get("/{id}", "someKey")).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void thatShortenerCreatesARedirectIfTheURLisOK() throws Exception {
        configureTransparentSave();
        when(validUrl.checkSyntax()).thenReturn(true);

        mockMvc.perform(post("/short").param("uri", "http://example.com/")
                .param("periodicity", "true")
                .param("qr", "false"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.target", is("http://example.com/")))
                .andExpect(jsonPath("$.sponsor", is(nullValue())));
    }

    @Test
    public void thatShortenerCreatesARedirectWithSponsor() throws Exception {
        configureTransparentSave();
        when(validUrl.checkSyntax()).thenReturn(true);

        mockMvc.perform(
                post("/short").param("uri", "http://example.com/").param(
                        "sponsor", "http://sponsor.com/")
                        .param("periodicity", "true")
                        .param("qr", "false")).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.target", is("http://example.com/")))
                .andExpect(jsonPath("$.sponsor", is("http://sponsor.com/")));
    }

    @Test
    public void thatShortenerFailsIfTheURLisWrong() throws Exception {
        configureTransparentSave();
        when(validUrl.checkSyntax()).thenReturn(false);

        mockMvc.perform(post("/short").param("uri", "someKey")
                .param("periodicity", "true")
                .param("qr", "false")).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void thatShortenerFailsIfTheRepositoryReturnsNull() throws Exception {
        when(validUrl.checkSyntax()).thenReturn(true);
        when(shortURLRepository.save(any(ShortURL.class))).thenReturn(null);

        mockMvc.perform(post("/short").param("uri", "someKey")
                .param("periodicity", "true")
                .param("qr", "false")).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void thatCheckStatusIsTrueIfPeriodicityIsTrue() throws Exception {
        configureTransparentSave();
        when(validUrl.checkSyntax()).thenReturn(true);

        mockMvc.perform(post("/short").param("uri", "http://example.com/")
                .param("periodicity", "true")
                .param("qr", "false"))
                .andDo(print())
                .andExpect(jsonPath("$.checkStatus", is(true)));
    }

    @Test
    public void thatCheckStatusIsFalseIfPeriodicityIsFalse() throws Exception {
        configureTransparentSave();
        when(validUrl.checkSyntax()).thenReturn(true);

        mockMvc.perform(post("/short").param("uri", "http://example.com/")
                .param("periodicity", "false")
                .param("qr", "false"))
                .andDo(print())
                .andExpect(jsonPath("$.checkStatus", is(false)));
    }

    @Test
    public void thatAliveIsOkIfPeriodicityWasTrue() throws Exception {
        when(shortURLRepository.findByKey(any(String.class))).thenReturn(ShortURLFixture.urlPeriodicity());
        mockMvc.perform(get("/{id}/alive", "someKey")).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void thatAliveFailsIfPeriodicityWasFalse() throws Exception {
        when(shortURLRepository.findByKey(any(String.class))).thenReturn(ShortURLFixture.urlNotPeriodicity());
        mockMvc.perform(get("/{id}/alive", "someKey")).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void thatAliveFailsIfIdKeyDoesNotExist() throws Exception {
        when(shortURLRepository.findByKey(any(String.class))).thenReturn(null);
        mockMvc.perform(get("/{id}/alive", "someKey")).andDo(print())
                .andExpect(status().isNotFound());
    }

    private void configureTransparentSave() {
        when(shortURLRepository.save(any(ShortURL.class)))
                .then((Answer<ShortURL>) invocation -> (ShortURL) invocation.getArguments()[0]);
    }
}