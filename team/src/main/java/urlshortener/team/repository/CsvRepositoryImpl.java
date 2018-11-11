package urlshortener.team.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import urlshortener.team.domain.CsvFormat;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CsvRepositoryImpl implements CsvRepository {
    @Override
    public List<String> parserCsv(MultipartFile file) {
        List<String> list = new ArrayList<>();
        list.add("http://uri1.com");
        list.add("http://uri2.com");
        return list;
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
