package urlshortener.team.web.rest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.team.repository.JobRepository;
import urlshortener.team.service.JobService;
import urlshortener.team.web.rest.fixture.CsvFixture;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CsvControllerTests {

    private MockMvc mockMvc;

    @Mock
    protected JobRepository jobRepository;

    @Mock
    protected JobService jobService;

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
        when(jobRepository.save(any())).thenAnswer((InvocationOnMock invocation) -> invocation.getArguments()[0]);

        // For void functions
        doNothing().when(jobService).processJob(any(), any(), any(), any());

        mockMvc.perform(multipart("/uploadCSV")
                .file(file))
                .andExpect(status().is(202))
                .andExpect(redirectedUrlPattern("http://*/job/*"));
    }


    @Test
    public void thatFileIsWrongParsingIt() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "application/csv", "URis to short".getBytes());
        when(jobService.parserCsv(file)).thenReturn(null);

        mockMvc.perform(multipart("/uploadCSV")
                .file(file))
                .andExpect(status().is(400));
    }

    @Test
    public void thatJobDoesntExist() throws Exception {
        when(jobRepository.findByKey(any())).thenReturn(null);

        mockMvc.perform(get("/job/{id}", "someKey")).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void thatJobIsAnsweringOk() throws Exception {
        when(jobRepository.findByKey(any())).thenReturn(CsvFixture.jobNotFinished());

        mockMvc.perform(get("/job/{id}", "someKey")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hash", is("0")))
                .andExpect(jsonPath("$.converted", is(3)))
                .andExpect(jsonPath("$.total", is(10)));
    }

    @Test
    public void thatResultDoesntExist() throws Exception {
        when(jobRepository.findByKey(any())).thenReturn(CsvFixture.jobNotFinished());

        mockMvc.perform(get("/result/{id}", "someKey")).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void thatResultIsOk() throws Exception {
        when(jobRepository.findByKey(any())).thenReturn(CsvFixture.jobFinished());

        mockMvc.perform(get("/result/{id}", "someKey")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"));
    }
}