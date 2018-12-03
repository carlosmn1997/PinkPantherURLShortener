package urlshortener.team.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import urlshortener.team.domain.CsvFormat;
import urlshortener.team.domain.ValidUrl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CsvRepositoryImpl implements CsvRepository {
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
                boolean ok = url.check();
                if(ok){
                    result.add(line);
                }
                else{
                    return null;
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    @Override
    public List<String> shortUris(List<String> urisToShort){
        List<String> urisShorted = new ArrayList<>();
        for(String uri : urisToShort){
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
        while(i < original.size()){
            CsvFormat file = new CsvFormat(original.get(i), formatted.get(i));
            list.add(file);
            i++;
        }
        return list;
    }
}
