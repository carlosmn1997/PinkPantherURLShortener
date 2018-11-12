package urlshortener.team.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.team.domain.URLChecker;
import urlshortener.team.repository.fixture.ShortAndCheckFixture;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ShortAndCheckControllerTests {

    private MockMvc mockMvc;

    @Mock
    private URLChecker c;

    @InjectMocks
    private ShortAndCheckController checkerAndShortener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(checkerAndShortener).build();
    }

    @Test
    public void thatUriIsReachable()
            throws Exception {
        when(c.checkUri("www.prueba.com")).thenReturn(ShortAndCheckFixture.isReachable());

        mockMvc.perform(post("/shortAndCheck").param("uri", "www.prueba.com")).andDo(print())
                .andExpect(status().isCreated()).andExpect(content().json("{\n" +
                "  \"hash\": \"1\",\n" +
                "  \"target\": \"www.prueba.com\",\n" +
                "  \"uri\": \"www.prueba.com\",\n" +
                "  \"sponsor\": \"A\",\n" +
                "  \"created\": 1,\n" +
                "  \"owner\": \"A\",\n" +
                "  \"mode\": 1,\n" +
                "  \"safe\": true,\n" +
                "  \"ip\": \"ip\",\n" +
                "  \"country\": \"Spain\"\n" +
                "}"));;
    }

    @Test
    public void thatUriIsNotReachable()
            throws Exception {
        when(c.checkUri("www.prueba.com")).thenReturn(ShortAndCheckFixture.isNotReachable());
        mockMvc.perform(post("/shortAndCheck").param("uri", "www.prueba.com")).andDo(print())
                .andExpect(status().isNotFound());
    }
}
