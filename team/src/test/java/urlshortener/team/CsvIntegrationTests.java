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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// https://www.baeldung.com/spring-rest-template-multipart-upload
// https://github.com/eugenp/tutorials/blob/master/spring-rest-template/src/main/java/com/baeldung/web/upload/client/MultipartFileUploadClient.java

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class CsvIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private static Resource getTestFile() throws IOException {
        Path testFile = Files.createTempFile("test-file", ".csv");
        System.out.println("Creating and Uploading Test File: " + testFile);
        Files.write(testFile, "http://www.uri1.com\nhttp://www.uri2.com\nhttp://www.uri3.com\n".getBytes());
        return new FileSystemResource(testFile.toFile());
    }

    private static Resource getTestFileIncorrect() throws IOException {
        Path testFile = Files.createTempFile("test-file", ".csv");
        System.out.println("Creating and Uploading Test File: " + testFile);
        Files.write(testFile, "httap://www.uri1.com\nhttp://www.uri2.com\nhttp://www.uri3.com\n".getBytes());
        return new FileSystemResource(testFile.toFile());
    }

    @Test
    public void testCsvCorrect() throws Exception {
        Resource testFile = getTestFile();
        ResponseEntity<String> entity = uploadCsv(testFile);

        String location = entity.getHeaders().getLocation().toString();
        char idJob = location.charAt(location.length()-1); // Assuming it is only one char

        assertThat(entity.getStatusCode(), is(HttpStatus.ACCEPTED));
        assertThat(location, is("http://localhost:" + port + "/job/"+idJob));

        // Get the job
        entity = restTemplate.getForEntity("/job/"+idJob, String.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
        ReadContext rc = JsonPath.parse(entity.getBody());

        while (rc.read("$.uriResult") == null) {
            entity = restTemplate.getForEntity("/job/"+idJob, String.class);
            assertThat(entity.getStatusCode(), is(HttpStatus.OK));
            ;

            String time = entity.getHeaders().getFirst("Retry-After");
            assertThat(time, is("1"));
            Thread.sleep(Integer.parseInt(time) * 1000);
            rc = JsonPath.parse(entity.getBody());
        }

        // Get the result
        entity = restTemplate.getForEntity("/result/"+idJob, String.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
        assertThat(entity.getHeaders().getContentType(), is(new MediaType("text", "csv", Charset.forName("ISO-8859-1"))));
    }

    @Test
    public void testCsvIncorrect() throws Exception {
        Resource testFile = getTestFileIncorrect();
        ResponseEntity<String> entity = uploadCsv(testFile);

        assertThat(entity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    private ResponseEntity<String> uploadCsv(Resource file) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(parts, headers);
        return restTemplate.postForEntity("/uploadCSV", requestEntity, String.class);
    }


}