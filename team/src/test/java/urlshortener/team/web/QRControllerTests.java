package urlshortener.team.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.team.domain.ShortURL;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static urlshortener.team.web.fixture.QRFixture.someUrlwithNoQr;
import static urlshortener.team.web.fixture.QRFixture.someUrlwithQr;
import static urlshortener.team.web.fixture.QRFixture.someUrlwithQrProgress;

public class QRControllerTests {
    private MockMvc mockMvc;

    @Mock
    private ShortURLRepository shortURLRepository;

    @InjectMocks
    private QRController qr;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(qr).build();
    }

    @Test
    public void thatDetectsBadId()
            throws Exception {
        when(shortURLRepository.findByKey("someKey")).thenReturn(null);
        mockMvc.perform(get("/{id}/qr", "someKey")).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void thatDetectsNoQR()
            throws Exception {
        when(shortURLRepository.findByKey("someKey")).thenReturn(someUrlwithNoQr());
        mockMvc.perform(get("/{id}/qr", "someKey")).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void thatDetectsQrInProgress()
            throws Exception {
        when(shortURLRepository.findByKey("someKey")).thenReturn(someUrlwithQrProgress());
        mockMvc.perform(get("/{id}/qr", "someKey")).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void thatSendsQR()
            throws Exception {
        when(shortURLRepository.findByKey("someKey")).thenReturn(someUrlwithQr());
        mockMvc.perform(get("/{id}/qr", "someKey")).andDo(print())
                .andExpect(status().isOk());
    }

}
