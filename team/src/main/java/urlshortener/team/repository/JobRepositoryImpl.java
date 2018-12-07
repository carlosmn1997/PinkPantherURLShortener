package urlshortener.team.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Async;
import urlshortener.team.domain.CsvFormat;
import urlshortener.team.domain.Job;
import urlshortener.team.domain.ShortURL;

import java.io.*;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JobRepositoryImpl implements JobRepository {

  private static final RowMapper<Job> rowMapper = (rs, rowNum) -> {
    List<CsvFormat> result = null;
    Blob b = rs.getBlob("result");
    if (b != null) {
      InputStream bis = b.getBinaryStream();
      try {
        ObjectInputStream ois = new ObjectInputStream(bis);
        result = (List<CsvFormat>) ois.readObject();

      } catch (Exception e) {
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
  public Job findByKey(String id) {
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
  public Job save(Job j1) {
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

  private Blob listToBlob(List<CsvFormat> result) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
        ObjectOutputStream ooStream = new ObjectOutputStream(baos);
        ooStream.writeObject(result);
        ooStream.close();
      } catch (IOException e) {
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
    } catch (Exception e) {

    }
    return null;
  }

  @Override
  public void update(Job j1) {
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
}
