package urlshortener.team.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import urlshortener.team.domain.CsvFormat;
import urlshortener.team.domain.Job;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.domain.ValidUrl;
import urlshortener.team.repository.JobRepository;
import urlshortener.team.repository.ShortURLRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ShortURLRepository shortURLRepository;

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
  public List<String> shortUris(List<String> urisToShort, Job job) {
    List<String> urisShorted = new ArrayList<>();
    for (String uri : urisToShort) {
      // Save URI
      ShortURL su = new ShortURL(uri, null, null, false, false);
      su = shortURLRepository.save(su);
      if (su != null) {
        urisShorted.add(su.getUri().toString());
      } else {
        urisShorted.add("No alcanzable");
      }
      //urisShorted.add("http://localhostMock:8080/123");
      System.out.println("Llevo: " + job.getConverted());
      job.setConverted(job.getConverted() + 1);
      jobRepository.update(job);
      System.out.println("DESPUES UPDATE");
      try {
        //TimeUnit.SECONDS.sleep(5);
      } catch (Exception e) {
        e.printStackTrace();
      }
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

  @Override
   public void generateCsvResponse(Job j, HttpServletResponse response){
        try{
            String csvFileName = "mock.csv";

            response.setContentType("text/csv");

            // creates mock data
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"",
                    csvFileName);
            response.setHeader(headerKey, headerValue);

            List<CsvFormat> csvList = j.getResult();
            // uses the Super CSV API to generate CSV data from the model data
            ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                    CsvPreference.STANDARD_PREFERENCE);

            String[] header = {
                    "URIOriginal",
                    "URIAcortada"
            };

            csvWriter.writeHeader(header);

            for (CsvFormat aFile : csvList) {
                csvWriter.write(aFile, header);
            }

            csvWriter.close();
        } catch(IOException e){
            e.printStackTrace();
        }
  }

  @Override
  @Async
  public void processJob(Job job, List<String> urisToShort) {
    List<String> urisShorted = shortUris(urisToShort, job);

    // when it has finished, create CSV
    List<CsvFormat> csvList = createCsv(urisToShort, urisShorted);
    job = jobRepository.findByKey(job.getHash()); // Because it has been updated
    job.setResult(csvList);
    jobRepository.update(job);
  }
}
