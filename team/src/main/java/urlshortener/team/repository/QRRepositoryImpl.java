package urlshortener.team.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.client.RestTemplate;
import urlshortener.team.domain.ShortURL;

import java.sql.Blob;

public class QRRepositoryImpl implements QRRepository {

  private static final RowMapper<ShortURL> rowMapper = (rs, rowNum) -> {
    byte[] image = null;
    Blob b = rs.getBlob("qrImage");
    if (b != null) {
      try {
        int blobLength = (int) b.length();
        image = b.getBytes(1, blobLength);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return new ShortURL(rs.getString("hash"), rs.getString("target"),
            null, rs.getString("sponsor"), rs.getDate("created"),
            rs.getString("owner"), rs.getInt("mode"),
            rs.getBoolean("safe"), rs.getString("ip"),
            rs.getString("country"), rs.getBoolean("checkStatus"),
            rs.getBoolean("aliveOnLastCheck"), rs.getBoolean("qr"),
            image);
  };
  private JdbcTemplate jdbc;

  public QRRepositoryImpl(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public boolean createQR(String hash, String uri) {

    try {
      jdbc.update("UPDATE SHORTURL SET qr = TRUE WHERE HASH = ?", hash);

      RestTemplate restTemplate = new RestTemplate();
      byte[] pngData = restTemplate.getForObject(
              "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + uri, byte[].class);
      Blob b = new javax.sql.rowset.serial.SerialBlob(pngData);

      jdbc.update("UPDATE SHORTURL SET qrImage = ? WHERE HASH = ?", b, hash);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
