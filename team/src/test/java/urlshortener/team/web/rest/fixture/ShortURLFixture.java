package urlshortener.team.web.rest.fixture;

import urlshortener.team.domain.ShortURL;

public class ShortURLFixture {

  public static ShortURL someUrl() {
    return new ShortURL("someKey", "http://example.com/", null, null, null,
            null, 307, true, null, null, null, null, null);
  }

  public static ShortURL someUrlWithSponsor() {
    return new ShortURL("someKey", "http://example.com/", null, "http://pinkpanther.com/", null,
            null, 307, true, null, null, null, null, null);
  }

  public static ShortURL urlNotPeriodicity() {
    return new ShortURL("1", "http://www.unizar.es/", null, null, null, null, null, false,
            null, null, false, false, null);
  }

  public static ShortURL urlPeriodicity() {
    return new ShortURL("1", "http://www.unizar.es/", null, null, null, null, null, false,
            null, null, true, false, null);
  }
}
