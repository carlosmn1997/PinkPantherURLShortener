package urlshortener.team.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.team.stompMessages.ReadyMessage;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

@RestController
public class SponsorController {
	@Autowired
	protected ShortURLRepository shortURLRepository;

	@Autowired
	protected ClickRepository clickRepository;

	private static String htmlTemplate = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>Redirecting...</title>\n" +
            "    <script src=\"https://code.jquery.com/jquery-3.3.1.min.js\" type=\"text/javascript\"></script>\n" +
            "    <script src=\"js/sponsor.js\" type=\"text/javascript\"></script>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div style=\"width:100%;height:50px;position:fixed;top:0px;\n" +
            "        background-color:#ff3399;z-index:99999;\">\n" +
            "        <span id=\"sponsor-skip-button\">Skip</span>\n" +
            "        <span id=\"sponsor-countdown\" style=\"display:none;\">Wait 5 seconds...</span>\n" +
            "    </div>\n" +
            "    <div id=\"sponsor-body\" style=\"position:absolute;top:50px;\">${sponsorhtml}</div>\n" +
            "</body>\n" +
            "</html>";

	@RequestMapping(value = "/sponsor/{id:(?!link).*}", method = RequestMethod.GET)
	public ResponseEntity<String> redirectToSponsor(@PathVariable String id,
			HttpServletRequest request) {

        ShortURL shorted = shortURLRepository.findByKey(id);
        if (shorted != null) {
            String sponsor = shorted.getSponsor();
            if (sponsor != null) {
                HttpHeaders h = new HttpHeaders();
                return new ResponseEntity<>(shorted.getSponsor(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        else{
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("URI acortada no existe");
        }
	}

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

	private static int id = 0;


	// The client is ready to wait
    @MessageMapping("/waitingSponsor")
    public void setTimer (@Payload ReadyMessage message) {
        ShortURL shorted = shortURLRepository.findByKey(message.getContent());
        if (shorted != null) {
            timer(id++, shorted.getTarget());
        }
    }

    @Async
    public void timer(int idTimer, String originalUri){
        try{
            Thread.sleep(5000); // Wait for 5 secs

            // Notify with the sponsor to the client
            messagingTemplate.convertAndSend("/queue/reply-" + idTimer, originalUri);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

	private static String responseToString(HttpURLConnection con) throws IOException{
        BufferedReader bfr = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String line = null;
        StringBuffer content = new StringBuffer();
        while((line = bfr.readLine()) != null){
            content.append(line);
        }
        bfr.close();
        return new String(content);
    }

	public static String generateHtml(ShortURL l){
	    try {
	        // https://www.baeldung.com/java-http-request
            URL urlSponsor = new URL(l.getSponsor());
            HttpURLConnection con = (HttpURLConnection) urlSponsor.openConnection();
            con.setRequestMethod("GET");
            con.setReadTimeout(6000);
            con.setConnectTimeout(6000);

            if(con.getResponseCode() != 200){
                con.disconnect();
                return htmlTemplate.replace("${sponsorhtml}","default");
            }else{
                String sponsorHtml = responseToString(con);
                con.disconnect();
                return htmlTemplate.replace("${sponsorhtml}",sponsorHtml);
            }
        } catch (MalformedURLException e){
	        return htmlTemplate.replace("${sponsorhtml}","default");
        } catch (IOException e){
            return htmlTemplate.replace("${sponsorhtml}","default");
        }
    }
}
