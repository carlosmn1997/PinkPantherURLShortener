package urlshortener.team.repository;

import org.springframework.web.multipart.MultipartFile;
import urlshortener.team.domain.CsvFormat;

import java.util.List;
import java.io.FileReader;

public interface CsvRepository {

    List<String> parserCsv(MultipartFile file);

    // Return the location of the file
    List<CsvFormat> createCsv(List<String> original, List<String> formatted);
}
