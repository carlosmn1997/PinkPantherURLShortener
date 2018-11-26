package urlshortener.team.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.CsvRepository;
import urlshortener.team.repository.JobRepository;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.team.web.fixture.CsvFixture;
import urlshortener.team.web.fixture.ShortURLFixture;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CsvControllerTests {

    private MockMvc mockMvc;

    @Mock
    private CsvRepository csvRepository;

    @Mock
    protected JobRepository jobRepository;

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
        doNothing().when(jobRepository).processJob(any(), any());
        //when(jobRepository.processJob(any(), ArgumentMatchers.<String>anyList())).thenAnswer((InvocationOnMock invocation) -> invocation.getArguments()[0]);
        //when(jobRepository.processJob(any(), null)).thenAnswer((InvocationOnMock invocation) -> invocation.getArguments()[0]);


        mockMvc.perform(multipart("/uploadCSV")
                .file(file))
                .andExpect(status().is(201))
                .andExpect(content().string("http://localhost:8080/job/0"));
                //.andDo(print());
    }

    @Test
    public void thatFileIsWrongParsingIt() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "application/csv", "URis to short".getBytes());
        when(csvRepository.parserCsv(file)).thenReturn(null);

        mockMvc.perform(multipart("/uploadCSV")
                .file(file))
                .andExpect(status().is(400));
                //.andDo(print());
    }

    @Test
    public void thatJobIsAnsweringOk() throws Exception {
        when(jobRepository.findByKey(any())).thenReturn(CsvFixture.jobNotFinished());

     //   mockMvc.perform(get("/job/{id}", "someKey").header("Origin","*")).andDo(print())
       //         .andExpect(status().isOk());
                //.andExpect(jsonPath("$.hash", is("0")))
                //.andExpect(jsonPath("$.converted", is("3")))
                //.andExpect(jsonPath("$.total", is("10")));
    }

    @Test
    public void thatResultIsOk() throws Exception {
        when(jobRepository.findByKey(any())).thenReturn(CsvFixture.jobFinished());

        mockMvc.perform(get("/result/{id}", "someKey")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"));
    }
}
