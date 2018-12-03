package urlshortener.team.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.List;

import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.team.repository.ShortURLRepositoryImpl;
import urlshortener.team.repository.fixture.ShortURLFixture;

import javax.imageio.ImageIO;

import static org.junit.Assert.*;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

public class QRRepositoryTests {

    private EmbeddedDatabase db;
    private QRRepository repository;
    private JdbcTemplate jdbc;
    private byte[] b;

    @Before
    public void setup() {
        db = new EmbeddedDatabaseBuilder().setType(HSQL)
                .addScript("schema-hsqldb.sql").build();
        jdbc = new JdbcTemplate(db);
        jdbc.update("INSERT INTO SHORTURL (hash) VALUES ('1')");
        try {
            File imgPath = new File("C:\\Users\\Nicolas\\IdeaProjects\\" +
                    "PinkPantherURLShortener\\team\\src\\test\\java\\urlshortener\\" +
                    "team\\web\\fixture\\descarga.png");
            BufferedImage bufferedImage = ImageIO.read(imgPath);
            WritableRaster raster = bufferedImage.getRaster();
            DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
            b = data.getData();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        repository = new QRRepositoryImpl(jdbc);
    }

    @Test
    public void thatCreatesWell(){
        assertEquals(repository.createQR("1","http://localhost:8080/1"),true);
        assertNotEquals(jdbc.queryForObject("select qrImage from SHORTURL",byte[].class),null);
        assertNotNull(jdbc.queryForObject("select qrImage from SHORTURL",byte[].class));
        assertEquals(jdbc.queryForObject("select qr from SHORTURL",Boolean.class),true);
    }

    @After
    public void shutdown() {
        jdbc.update("DELETE FROM SHORTURL WHERE hash= '1'");
        db.shutdown();
    }

}
