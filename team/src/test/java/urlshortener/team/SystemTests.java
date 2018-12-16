package urlshortener.team;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.nio.charset.Charset;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class SystemTests {

  @Autowired
  private TestRestTemplate restTemplate;


  @Test
  public void testHome() {
    ResponseEntity<String> entity = restTemplate.getForEntity("/", String.class);
    assertThat(entity.getStatusCode(), is(HttpStatus.OK));
    assertTrue(entity.getHeaders().getContentType().isCompatibleWith(new MediaType("text", "html")));
    assertThat(entity.getBody(), containsString("<title>URL"));
  }

  @Test
  public void testCss() {
    ResponseEntity<String> entity = restTemplate.getForEntity("/webjars/bootstrap/3.3.5/css/bootstrap.min.css", String.class);
    assertThat(entity.getStatusCode(), is(HttpStatus.OK));
    assertThat(entity.getHeaders().getContentType(), is(MediaType.valueOf("text/css")));
    assertThat(entity.getBody(), containsString("body"));
  }

  @Test
  public void testCreateLinkFailsIfNoPeriodicity() throws Exception {
    ResponseEntity<String> entity = postLink("http://example.com/", null, true, null);
    assertThat(entity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
  }

  @Test
  public void testCreateLinkFailsIfNoQr() throws Exception {
    ResponseEntity<String> entity = postLink("http://example.com/", true, null, null);
    assertThat(entity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
  }

  @Test
  public void testCreateLinkWithTruePeriodicity() throws Exception {
    ResponseEntity<String> entity = postLink("http://example.com/", true, false, null);
    assertThat(entity.getStatusCode(), is(HttpStatus.CREATED));
    assertThat(entity.getHeaders().getContentType(), is(new MediaType("application", "json", Charset.forName("UTF-8"))));
    ReadContext rc = JsonPath.parse(entity.getBody());
    assertThat(rc.read("$.checkStatus"), is(true));
  }

  @Test
  public void testCreateLinkRWithFalsePeriodicity() throws Exception {
    ResponseEntity<String> entity = postLink("http://example.com/", false, false, null);
    assertThat(entity.getStatusCode(), is(HttpStatus.CREATED));
    assertThat(entity.getHeaders().getContentType(), is(new MediaType("application", "json", Charset.forName("UTF-8"))));
    ReadContext rc = JsonPath.parse(entity.getBody());
    assertThat(rc.read("$.checkStatus"), is(false));
  }

  @Test
  public void testCreateLinkWithTrueQr() throws Exception {
    ResponseEntity<String> entity = postLink("http://example.com/", true, true, null);
    assertThat(entity.getStatusCode(), is(HttpStatus.CREATED));
    assertThat(entity.getHeaders().getContentType(), is(new MediaType("application", "json", Charset.forName("UTF-8"))));
    ReadContext rc = JsonPath.parse(entity.getBody());
    assertThat(rc.read("$.qr"), is(true));
  }

  @Test
  public void testCreateLinkWithFalseQr() throws Exception {
    ResponseEntity<String> entity = postLink("http://example.com/", true, false, null);
    assertThat(entity.getStatusCode(), is(HttpStatus.CREATED));
    assertThat(entity.getHeaders().getContentType(), is(new MediaType("application", "json", Charset.forName("UTF-8"))));
    ReadContext rc = JsonPath.parse(entity.getBody());
    assertThat(rc.read("$.qr"), is(false));
  }

  @Test
  public void testCreateLinkWithoutSponsor() throws Exception {
    ResponseEntity<String> entity = postLink("http://example.com/", false, false, null);

    assertThat(entity.getStatusCode(), is(HttpStatus.CREATED));
    assertThat(entity.getHeaders().getContentType(), is(new MediaType("application", "json", Charset.forName("UTF-8"))));
    ReadContext rc = JsonPath.parse(entity.getBody());
    assertThat(rc.read("$.sponsor"), is(nullValue()));
  }

  @Test
  public void testCreateLinkWithSponsor() throws Exception {
    ResponseEntity<String> entity = postLink("http://example.com/", false, false, "http://www.misponsor.com");

    assertThat(entity.getStatusCode(), is(HttpStatus.CREATED));
    assertThat(entity.getHeaders().getContentType(), is(new MediaType("application", "json", Charset.forName("UTF-8"))));
    ReadContext rc = JsonPath.parse(entity.getBody());
    assertThat(rc.read("$.sponsor"), is("http://www.misponsor.com"));
  }

  @Test
  public void testRedirection() throws Exception {
    ResponseEntity<String> entity = postLink("http://example.com/", true, true, null);

    ReadContext rc = JsonPath.parse(entity.getBody());
    entity = restTemplate.getForEntity("/" + rc.read("$.hash"), String.class);
    assertThat(entity.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));
    assertThat(entity.getHeaders().getLocation(), is(new URI("http://example.com/")));
  }

  @Test
  public void testBadRedirection() throws Exception {
    ResponseEntity<String> entity = restTemplate.getForEntity("/someKey", String.class);

  }

  @Test
  public void testAliveWhenPeriodicityIsFalse() throws Exception {
    ResponseEntity<String> entity = postLink("http://marca.com/", false, true, null);

    ReadContext rc = JsonPath.parse(entity.getBody());
    entity = restTemplate.getForEntity("/" + rc.read("$.hash") + "/alive", String.class);
    assertThat(entity.getStatusCode(), is(HttpStatus.NOT_FOUND));
  }

  @Test
  public void testAliveWhenPeriodicityIsTrue() throws Exception {
    ResponseEntity<String> entity = postLink("http://example.com/", true, true, null);

    ReadContext rc = JsonPath.parse(entity.getBody());
    entity = restTemplate.getForEntity("/" + rc.read("$.hash") + "/alive", String.class);
    assertThat(entity.getStatusCode(), is(HttpStatus.OK));
    assertThat(rc.read("$.checkStatus"), is(true));
    assertThat(rc.read("$.aliveOnLastCheck"), anyOf(is(true), is(false)));
  }

  @Test
  public void testAliveWhenKeyDoesNotExist() throws Exception {
    ResponseEntity<String> entity = restTemplate.getForEntity("/someKey/alive", String.class);
    assertThat(entity.getStatusCode(), is(HttpStatus.NOT_FOUND));
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