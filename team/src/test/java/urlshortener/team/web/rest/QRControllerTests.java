package urlshortener.team.web.rest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.team.repository.ShortURLRepository;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static urlshortener.team.web.rest.fixture.QRFixture.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class QRControllerTests {

  private MockMvc mockMvc;

  @Mock
  protected ShortURLRepository shortURLRepository;

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
