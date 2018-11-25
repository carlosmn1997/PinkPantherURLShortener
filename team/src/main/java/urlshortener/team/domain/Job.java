package urlshortener.team.domain;

import java.net.URI;
import java.sql.Date;
import java.util.List;

public class Job {

	private String hash;
	private int converted;
	private int total;
	private URI uriResult;
	private List<CsvFormat> result; // is a CSV

	private Job(String hash, int total){
		this.hash = hash;
		this.total = total;
	}

    public Job() {
	}

    public Job(String hash, int converted, int total, URI uriResult, List<CsvFormat> result) {
        this.hash = hash;
        this.converted = converted;
        this.total = total;
        this.uriResult = uriResult;
        this.result = result;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getConverted() {
        return converted;
    }

    public void setConverted(int converted) {
        this.converted = converted;
    }

    public List<CsvFormat> getResult() {
        return result;
    }

    public void setResult(List<CsvFormat> result) {
        this.result = result;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public URI getUriResult() {
        return uriResult;
    }

    public void setUriResult(URI uriResult) {
        this.uriResult = uriResult;
    }
}
