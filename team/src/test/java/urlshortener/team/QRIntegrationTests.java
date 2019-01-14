package urlshortener.team;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class QRIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int serverPort;

    @Test
    public void testReturnsErrorWhenNotCreated() throws Exception {
        ResponseEntity<String> entity = postLink("http://example2.com/", false, false, null);

        ReadContext rc = JsonPath.parse(entity.getBody());
        ResponseEntity<?> qrResponse =
                restTemplate.getForEntity("/" + rc.read("$.hash") + "/qr", String.class);
        assertThat(qrResponse.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Ignore
    public void testReturnsErrorWhenBadID() throws Exception {
        ResponseEntity<String> entity = postLink("http://example3.com/", false, true, null);

        ResponseEntity<?> qrResponse =
                restTemplate.getForEntity("/1234/qr", String.class);
        assertThat(qrResponse.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        ReadContext rc = JsonPath.parse(entity.getBody());
        qrResponse =
                restTemplate.getForEntity("/" + rc.read("$.hash") + "/qr", String.class);
        assertThat(qrResponse.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(qrResponse.getBody());
    }

    @Ignore
    public void testReturnsQRWhenCreated() throws Exception {
        ResponseEntity<String> entity = postLink("http://example.com/", false, true, null);

        ReadContext rc = JsonPath.parse(entity.getBody());
        ResponseEntity<?> qrResponse =
                restTemplate.getForEntity("/" + rc.read("$.hash") + "/qr", String.class);
        assertThat(qrResponse.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(qrResponse.getBody());
    }


    private ResponseEntity<String> postLink(String url, Boolean periodicity, Boolean qr, String sponsor) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("uri", url);
        if (periodicity != null) {
            parts.add("periodicity", periodicity);
        }
        if (qr != null) {
            parts.add("qr", qr);
        }
        if (sponsor != null) {
            parts.add("sponsor", sponsor);
        }
        return restTemplate.postForEntity("/short", parts, String.class);
    }

}
