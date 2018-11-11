package urlshortener.team.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.CsvRepository;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.team.web.fixture.ShortURLFixture;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CsvControllerTests {

    private MockMvc mockMvc;

    @Mock
    private CsvRepository csvRepository;

    @InjectMocks
    private CsvController csv;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(csv).build();
    }

    @Test
    public void thatCsvUploadResponseIsOK() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "application/csv", "URis to short".getBytes());

        mockMvc.perform(multipart("/uploadCSV")
                .file(file))
                .andExpect(status().is(200))
                .andExpect(content().contentType("text/csv"))
                .andDo(print());
    }
}
