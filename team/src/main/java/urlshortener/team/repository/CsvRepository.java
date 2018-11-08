package urlshortener.team.repository;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public interface CsvRepository {

    List<String> parsetCsv(FileReader file);

    // Return the location of the file
    String createCsv(List<String> original, List<String> formatted);
}
