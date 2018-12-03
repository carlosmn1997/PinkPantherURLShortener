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
import org.springframework.web.multipart.MultipartFile;
import urlshortener.team.repository.fixture.CsvRepositoryFixture;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// https://www.baeldung.com/spring-rest-template-multipart-upload
// https://github.com/eugenp/tutorials/blob/master/spring-rest-template/src/main/java/com/baeldung/web/upload/client/MultipartFileUploadClient.java

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= RANDOM_PORT)
@DirtiesContext
public class CsvIntegrationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;


	@Test
    @Ignore
	public void testCsvCorrect() throws Exception {
	    Resource testFile = getTestFile();
		ResponseEntity<String> entity = uploadCsv(testFile);

		assertThat(entity.getStatusCode(), is(HttpStatus.CREATED));
		assertThat(entity.getBody(), is("http://localhost:8080/job/0"));

		// Get the job
        entity = restTemplate.getForEntity( entity.getBody(), String.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
        ReadContext rc = JsonPath.parse(entity.getBody());

        // TODO hacer un bucle y retry after
        assertThat(rc.read("$.hash"), is("0"));
        assertThat(rc.read("$.converted"), is(0));
        assertThat(rc.read("$.total"), is(3));

        Thread.sleep(30000);

        // Get the result
        entity = restTemplate.getForEntity("http://localhost:8080/result/0", String.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
        assertThat(entity.getHeaders().getContentType(), is(new MediaType("text", "csv", Charset.forName("UTF-8"))));
    }

    @Test
    public void testCsvIncorrect() throws Exception {
        Resource testFile = getTestFileIncorrect();
        ResponseEntity<String> entity = uploadCsv(testFile);

        assertThat(entity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

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

	private ResponseEntity<String> uploadCsv(Resource file){
	    MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(parts, headers);
        return restTemplate.postForEntity("/uploadCSV", requestEntity, String.class);
    }



}