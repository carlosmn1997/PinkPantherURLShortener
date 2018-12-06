package urlshortener.team.repository;

public interface QRRepository {

  boolean createQR(String hash, String uri);

}
