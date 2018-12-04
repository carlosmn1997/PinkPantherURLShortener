package urlshortener.team.repository.fixture;

import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvRepositoryFixture {

  public static MultipartFile getCorrectCsv() {
    File file = new File("csvCorrect.csv");
    //File file = new File("C:\\Users\\Carlos\\Documents\\GitHub\\urlshortener\\team\\src\\test\\java\\urlshortener\\team\\repository\\fixture\\csvCorrect.csv");
    try {
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
