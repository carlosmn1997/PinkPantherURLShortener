package urlshortener.team.service.fixture;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvRepositoryFixture {

  public static MultipartFile getCorrectCsv() {
      // Classpath is resources
      Resource csv = new ClassPathResource("csvCorrect.csv");
      MultipartFile multipartFile = getMultipartFile(csv);
      if (multipartFile != null) return multipartFile;
      return null;
  }

    public static MultipartFile getIncorrectCsv() {
        // Classpath is resources
        Resource csv = new ClassPathResource("csvIncorrect.csv");
        MultipartFile multipartFile = getMultipartFile(csv);
        if (multipartFile != null) return multipartFile;
        return null;
    }

    private static MultipartFile getMultipartFile(Resource csv) {
        try {
            File file = csv.getFile();
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("myURIS",
                    file.getName(), "text/plain", IOUtils.toByteArray(input));
            return multipartFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> urisToShort() {
    ArrayList<String> uris = new ArrayList<>();
    uris.add("http://www.uri1.com");
    uris.add("http://www.uri2.com");
    uris.add("http://www.uri3.com");
    return uris;
  }

  public static List<String> urisShorted() {
    ArrayList<String> uris = new ArrayList<>();
    uris.add("http://www.base.com/1");
    uris.add("http://www.base.com/2");
    uris.add("http://www.base.com/3");
    return uris;
  }
}
