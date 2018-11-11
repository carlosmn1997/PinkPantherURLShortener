package urlshortener.team.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.team.repository.StatsRepository;
import urlshortener.team.repository.fixture.StatsFixture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatsControllerTests {
    private MockMvc mockMvc;

    @Mock
    private StatsRepository statsRepository;

    @InjectMocks
    private StatsController statsController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(statsController).build();
    }

    @Test
    public void thatStatsResponseIsOk()
            throws Exception {
        when(statsRepository.getStats()).thenReturn(StatsFixture.stats());

        mockMvc.perform(get("/stats")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"uptime_seconds\":1,\"user_number\":1," +
                        "\"uri_number\":1,\"click_number\":1,\"time_last_redirection\":1," +
                        "\"mem_used_mb\":1,\"mem_available_mb\":1}"));
    }
}
