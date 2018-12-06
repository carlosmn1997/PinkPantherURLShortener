package urlshortener.team.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import urlshortener.team.domain.CsvFormat;
import urlshortener.team.domain.ValidUrl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvServiceImpl implements CsvService {
  @Override
  public List<String> parserCsv(MultipartFile multipart) {
    BufferedReader br;
    List<String> result = new ArrayList<>();
    try {
      String line;
      InputStream is = multipart.getInputStream();
      br = new BufferedReader(new InputStreamReader(is));
      while ((line = br.readLine()) != null) {
        ValidUrl url = new ValidUrl(line);
        boolean ok = url.checkSyntax();
        if (ok) {
          result.add(line);
        } else {
          return null;
        }
      }

    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    return result;
  }

  @Override
  public List<String> shortUris(List<String> urisToShort) {
    List<String> urisShorted = new ArrayList<>();
    for (String uri : urisToShort) {
      // Shortening uri
      // ...
      urisShorted.add("http://localhostMock:8080/123");
    }
    return urisShorted;
  }

  @Override
  public List<CsvFormat> createCsv(List<String> original, List<String> formatted) {
    List<CsvFormat> list = new ArrayList<>();
    int i = 0;
    while (i < original.size()) {
      CsvFormat file = new CsvFormat(original.get(i), formatted.get(i));
      list.add(file);
      i++;
    }
    return list;
  }
}
