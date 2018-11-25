package urlshortener.team.repository;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import urlshortener.team.domain.CsvFormat;
import urlshortener.team.domain.Job;
import urlshortener.team.domain.ShortURL;

import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.net.URI;
import java.sql.Blob;
import java.util.List;

@Repository
public class JobRepositoryImpl implements JobRepository{

    private static final RowMapper<Job> rowMapper = (rs, rowNum) -> {
        List<CsvFormat> result = null;
        Blob b = rs.getBlob("result");
        InputStream bis = b.getBinaryStream();
        try{
            ObjectInputStream ois = new ObjectInputStream(bis);
            result = (List<CsvFormat>) ois.readObject();

        } catch(Exception e){
            e.printStackTrace();
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
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buff = bos.toByteArray();
            Blob drawingBlob = null;
            drawingBlob = new SerialBlob(buff);
            //j.setDrawingObject(drawingBlob);
            String kk2 = "chachi perfecto";
            byte[] kk = kk2.getBytes();

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
            Blob blob = new javax.sql.rowset.serial.SerialBlob(serializedData);

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

    @Override
    public void update(Job jl){

    }
}
