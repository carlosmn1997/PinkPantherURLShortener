package urlshortener.team.web.stomp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import urlshortener.team.Application;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.team.web.rest.fixture.ShortURLFixture;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext
public class SponsorControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ShortURLRepository shortURLRepository;

  private CompletableFuture<SponsorMessage> completableFuture;

  @Before
  public void setup(){
    completableFuture = new CompletableFuture<>();
  }


  @Test
  public void thatSponsorInfoIsOk() throws Exception {

    // Data setup

    mockMvc.perform(get("/waitingSponsor/info")).andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
  }

  @Test
  public void thatDelayedUriResolutionIsOk() throws InterruptedException, ExecutionException, TimeoutException {
    when(shortURLRepository.findByKey("someKey")).thenReturn(ShortURLFixture.someUrlWithSponsor());

    // https://medium.com/@MelvinBlokhuijzen/spring-websocket-endpoints-integration-testing-180357b4f24c

    WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(
            Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());

    StompSession stompSession = stompClient.connect("ws://localhost:8080/waitingSponsor",
            new StompSessionHandlerAdapter(){}).get(1,TimeUnit.SECONDS);

    assertTrue(stompSession.isConnected());

    stompSession.subscribe("/queue/reply-0", new StompFrameHandler() {
      @Override
      public Type getPayloadType(StompHeaders headers) {
        return SponsorMessage.class;
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
        completableFuture.complete((SponsorMessage) payload);
      }
    });

    stompSession.send("/app/waitingSponsor", new ReadyMessage("someKey","0"));

    SponsorMessage sponsorMessage = completableFuture.get(10,TimeUnit.SECONDS);
    assertNotNull(sponsorMessage);
  }
}
