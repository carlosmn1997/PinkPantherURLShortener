package urlshortener.team.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import urlshortener.team.domain.Click;

import java.util.List;

public interface QRRepository {

    boolean createQR(String hash,String uri);

}
