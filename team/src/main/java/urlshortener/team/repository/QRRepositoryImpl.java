package urlshortener.team.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import urlshortener.team.domain.Click;
import urlshortener.team.domain.CsvFormat;
import urlshortener.team.domain.Job;
import urlshortener.team.domain.ShortURL;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.Collections;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

@Repository
public class QRRepositoryImpl implements QRRepository {

    private JdbcTemplate jdbc;

    public QRRepositoryImpl(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final RowMapper<ShortURL> rowMapper = (rs, rowNum) -> {
        byte[] image= null;
        Blob b = rs.getBlob("qrImage");
        if (b != null){
            try{
                int blobLength = (int) b.length();
                image = b.getBytes(1, blobLength);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return new ShortURL(rs.getString("hash"), rs.getString("target"),
                null, rs.getString("sponsor"), rs.getDate("created"),
                rs.getString("owner"), rs.getInt("mode"),
                rs.getBoolean("safe"), rs.getString("ip"),
                rs.getString("country"), rs.getBoolean("checkStatus"),
                rs.getBoolean("aliveOnLastCheck"),rs.getBoolean("qr"),
                image);
    };

    @Override
    public boolean createQR(String uri) {

        try {
            jdbc.update("UPDATE SHORTURL SET qr = TRUE");

            RestTemplate restTemplate = new RestTemplate();
            byte[] pngData = restTemplate.getForObject(
                    "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data="+uri, byte[].class);
            Blob b = new javax.sql.rowset.serial.SerialBlob(pngData);

            jdbc.update("UPDATE SHORTURL SET qrImage = ?",b);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
