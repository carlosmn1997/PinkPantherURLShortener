package urlshortener.team.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import urlshortener.team.config.PersistenceContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@Import(PersistenceContext.class)
@JdbcTest
public class QRRepositoryTests {

  @Autowired
  private QRRepository repository;

  @Autowired
  private JdbcTemplate jdbc;
  private byte[] b;

  @Before
  public void setup() {
    jdbc.update("INSERT INTO SHORTURL (hash) VALUES ('1')");
    try {
      File imgPath = new File("C:\\Users\\Nicolas\\IdeaProjects\\" +
              "PinkPantherURLShortener\\team\\src\\test\\java\\urlshortener\\" +
              "team\\web\\fixture\\descarga.png");
      BufferedImage bufferedImage = ImageIO.read(imgPath);
      WritableRaster raster = bufferedImage.getRaster();
      DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
      b = data.getData();
    } catch (Exception e) {
      e.printStackTrace();
    }
    repository = new QRRepositoryImpl(jdbc);
  }

  @Test
  public void thatCreatesWell() {
    assertEquals(repository.createQR("1", "http://localhost:8080/1"), true);
    assertNotEquals(jdbc.queryForObject("select qrImage from SHORTURL", byte[].class), null);
    assertNotNull(jdbc.queryForObject("select qrImage from SHORTURL", byte[].class));
    assertEquals(jdbc.queryForObject("select qr from SHORTURL", Boolean.class), true);
  }

  @After
  public void shutdown() {
    jdbc.update("DELETE FROM SHORTURL WHERE hash= '1'");
  }

}
