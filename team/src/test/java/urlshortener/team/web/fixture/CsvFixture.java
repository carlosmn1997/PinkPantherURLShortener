package urlshortener.team.web.fixture;


import urlshortener.team.domain.CsvFormat;
import urlshortener.team.domain.Job;

import java.util.ArrayList;
import java.util.List;

public class CsvFixture {

        public static Job jobNotFinished() {
		    return new Job("0", 3, 10, null, null);
	    }

    public static Job jobFinished() {
        List<CsvFormat> example = new ArrayList<>();
        CsvFormat file1 = new CsvFormat("http://uriOriginal1.com", "http://uriAcortada1.com");
        CsvFormat file2 = new CsvFormat("http://uriOriginal2.com", "http://uriAcortada2.com");
        example.add(file1);
        example.add(file2);
        return new Job("0", 10, 10, null, example);
    }
}
