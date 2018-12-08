package urlshortener.team.web.rest;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import urlshortener.team.Application;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.ShortURLRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DirtiesContext
public class SponsorControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ClickRepository clickRepository;

  @Autowired
  private ShortURLRepository shortURLRepository;


  @Ignore
  public void thatRedirectToWithSponsorReturnsTemporaryRedirectIfKeyExists() throws Exception {

    // Data setup

    mockMvc.perform(get("/sponsor/{id}", "someKey")).andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string("http://pinkpanther.com/"));
  }
}
