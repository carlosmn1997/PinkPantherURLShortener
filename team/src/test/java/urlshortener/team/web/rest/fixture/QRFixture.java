package urlshortener.team.web.rest.fixture;

import urlshortener.team.domain.ShortURL;

import java.util.Random;

public class QRFixture {

  public static ShortURL someUrlwithQr() {
    byte[] b = new byte[20];
    new Random().nextBytes(b);
    return new ShortURL("someKey", "http://example.com/", null, null, null,
            null, 307, true, null, null, null, null, true, b);
  }

  public static ShortURL someUrlwithNoQr() {
    return new ShortURL("someKey", "http://example.com/", null, null, null,
            null, 307, true, null, null, null, null, false, null);
  }

  public static ShortURL someUrlwithQrProgress() {
    return new ShortURL("someKey", "http://example.com/", null, null, null,
            null, 307, true, null, null, null, null, true, null);
  }
}
