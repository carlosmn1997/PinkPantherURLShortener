package urlshortener.team.repository;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import urlshortener.team.domain.ApiResponse;
import urlshortener.team.domain.CsvFormat;
import urlshortener.team.domain.Job;
import urlshortener.team.domain.ShortURL;

import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.net.URI;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class JobRepositoryImpl implements JobRepository{

    private static final RowMapper<Job> rowMapper = (rs, rowNum) -> {
        List<CsvFormat> result = null;
        Blob b = rs.getBlob("result");
        if (b != null){
            InputStream bis = b.getBinaryStream();
            try{
                ObjectInputStream ois = new ObjectInputStream(bis);
                result = (List<CsvFormat>) ois.readObject();

            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return new Job(rs.getString("hash"), rs.getInt("converted"), rs.getInt("total"), null, result);
    };

    private JdbcTemplate jdbc;

    public JobRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Job findByKey(String id){
        try {
            return jdbc.queryForObject("SELECT * FROM job WHERE hash=?",
                    rowMapper, id);
        } catch (Exception e) {
            //log.debug("When select for key {}", id, e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Job save(Job j1){
        try {
            List<CsvFormat> result = j1.getResult();
            Blob blob = listToBlob(result);

            jdbc.update("INSERT INTO job VALUES (?,?,?,?)",
                    j1.getHash(), j1.getConverted(), j1.getTotal(), blob);
        } catch (DuplicateKeyException e) {
            return j1;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return j1;
    }

    private Blob listToBlob(List<CsvFormat> result){
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream ooStream = new ObjectOutputStream(baos);
                ooStream.writeObject(result);
                ooStream.close();
            } catch(IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    baos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            byte[] serializedData = baos.toByteArray();

            return new javax.sql.rowset.serial.SerialBlob(serializedData);
        } catch (Exception e){

        }
        return null;
    }

    @Override
    public void update(Job j1){
        try {

            List<CsvFormat> result = j1.getResult();
            Blob blob = listToBlob(result);

            jdbc.update(
                    "update job set hash=?, converted=?, total=?, result=? where hash=?",
                    j1.getHash(), j1.getConverted(), j1.getTotal(),
                    blob, j1.getHash());
        } catch (Exception e) {
            e.printStackTrace();
            //log.debug("When update for hash {}",  su.getHash(), e);
        }
    }

    @Override
    public List<String> shortUris(List<String> urisToShort, Job job){
        List<String> urisShorted = new ArrayList<>();
        for(String uri : urisToShort){
            // Shortening uri
            /*
            ShortURL su = createAndSaveIfValid(uri, sponsor, UUID
                    .randomUUID().toString(), extractIP(request), periodicity, qr);
            if (su != null) {
                return new ResponseEntity<>(su, h, HttpStatus.CREATED);
            } else {
                ApiResponse a = new ApiResponse(HttpStatus.BAD_REQUEST.value(), "INV",uri + " is not a valid url");
                return new ResponseEntity<>(a, HttpStatus.BAD_REQUEST);
            }
            */

            urisShorted.add("http://localhostMock:8080/123");
            System.out.println("Llevo: "+job.getConverted());
            job.setConverted(job.getConverted()+1);
            update(job);
            try{
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e){
                e.printStackTrace();
            }
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

    @Override
    @Async
    public void processJob(Job job, List<String> urisToShort){
        List<String> urisShorted = shortUris(urisToShort, job);

        // when it has finished, create CSV
        List<CsvFormat> csvList = createCsv(urisToShort, urisShorted);
        job = findByKey(job.getHash()); // Because it has been updated
        job.setResult(csvList);
        update(job);
    }
}
