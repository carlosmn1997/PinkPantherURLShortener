package urlshortener.team.repository;

import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import urlshortener.team.domain.CsvFormat;

import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;

public interface CsvRepository {

    List<String> parserCsv(MultipartFile file);

    List<String> shortUris(List<String> urisToShort);

    // Return the location of the file
    List<CsvFormat> createCsv(List<String> original, List<String> formatted);
}
