package urlshortener.team;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class StatsIntegrationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testStatsChangeAfterClickingLink() throws Exception {
        ResponseEntity<String> shortUriResponse = postLink("http://example.com/", false, false, null);
        assertThat(shortUriResponse.getStatusCode(), is(HttpStatus.CREATED));
        ReadContext createdShortUri = JsonPath.parse(shortUriResponse.getBody());

        ResponseEntity<String> statsEntity = restTemplate.getForEntity("/stats", String.class);
        assertThat(statsEntity.getStatusCode(),is(HttpStatus.OK));
        ReadContext initialStats = JsonPath.parse(statsEntity.getBody());

        shortUriResponse = restTemplate.getForEntity("/"+createdShortUri.read("$.hash"), String.class);
        assertThat(shortUriResponse.getStatusCode(),not(HttpStatus.NOT_FOUND));

        statsEntity = restTemplate.getForEntity("/stats", String.class);
        assertThat(statsEntity.getStatusCode(),is(HttpStatus.OK));
        ReadContext finalStats = JsonPath.parse(statsEntity.getBody());

        assertEquals((int)finalStats.read("$.user_number"),(int)initialStats.read("$.user_number")+1);
        assertEquals((int)finalStats.read("$.click_number"),(int)initialStats.read("$.click_number")+1);
    }

    @Test
    public void testStatsChangeAfterCreatingLink() throws Exception {
        ResponseEntity<String> statsEntity = restTemplate.getForEntity("/stats", String.class);
        assertThat(statsEntity.getStatusCode(),is(HttpStatus.OK));
        ReadContext initialStats = JsonPath.parse(statsEntity.getBody());

        ResponseEntity<String> entityShortURI = postLink("http://example2.com/", false, false, null);
        assertThat(entityShortURI.getStatusCode(), is(HttpStatus.CREATED));

        statsEntity = restTemplate.getForEntity("/stats", String.class);
        ReadContext finalStats = JsonPath.parse(statsEntity.getBody());

        assertEquals((int)finalStats.read("$.uri_number"),(int)initialStats.read("$.uri_number")+1);
    }

    @Test
    public void testUriStatsWhenKeyDoesNotExist() throws Exception {
        ResponseEntity<String> entity = restTemplate.getForEntity("/someKey/info", String.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void testUriStatsChangeAfterClickingLink() throws Exception {
        ResponseEntity<String> shortUriResponse = postLink("http://example3.com/", false, false, null);
        assertThat(shortUriResponse.getStatusCode(), is(HttpStatus.CREATED));
        ReadContext createdShortUri = JsonPath.parse(shortUriResponse.getBody());
        String hash = createdShortUri.read("$.hash");

        ResponseEntity<String> statsEntity = restTemplate.getForEntity("/"+hash+"/info", String.class);
        assertThat(statsEntity.getStatusCode(),is(HttpStatus.OK));
        ReadContext initialStats = JsonPath.parse(statsEntity.getBody());

        shortUriResponse = restTemplate.getForEntity("/"+hash, String.class);
        assertThat(shortUriResponse.getStatusCode(),not(HttpStatus.NOT_FOUND));

        statsEntity = restTemplate.getForEntity("/"+hash+"/info", String.class);
        assertThat(statsEntity.getStatusCode(),is(HttpStatus.OK));
        ReadContext finalStats = JsonPath.parse(statsEntity.getBody());

        assertEquals((int)finalStats.read("$.click_number"),(int)initialStats.read("$.click_number")+1);
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
