package urlshortener.team.web.stomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;

@Controller
public class SponsorController {

    @Autowired
    protected ShortURLRepository shortURLRepository;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @GetMapping("/waitingSponsor/info")
    public ResponseEntity<String> redirectToSponsor() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON_UTF8);
        h.setCacheControl(CacheControl.noCache());
        return new ResponseEntity<>(h, HttpStatus.OK);
    }

    // The client is ready to wait
    @MessageMapping("/waitingSponsor")
    public void setTimer(@Payload ReadyMessage message) {
        ShortURL shorted = shortURLRepository.findByKey(message.getContent());
        if (shorted != null) {
            //timer(id++, shorted.getTarget());
            timer(Integer.parseInt(message.getIdTimer()), shorted.getTarget());
        }
    }

    @Async
    public void timer(int idTimer, String originalUri) {
        try {
            Thread.sleep(5000); // Wait for 5 secs

            // Notify with the sponsor to the client
            messagingTemplate.convertAndSend("/queue/reply-" + idTimer, new SponsorMessage(originalUri));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
