package urlshortener.team.repository.fixture;

import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import urlshortener.team.domain.CsvFormat;
import urlshortener.team.domain.Job;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JobRepositoryFixture {

    public static Job jobExample(){
        // String hash, int converted, int total, URI uriResult, List<CsvFormat> result
        return new Job("0", 2, 10, null, null);
    }

    public static Job jobWithUris(){
        // String hash, int converted, int total, URI uriResult, List<CsvFormat> result
        return new Job("1", 0, 2, null, null);
    }
}
