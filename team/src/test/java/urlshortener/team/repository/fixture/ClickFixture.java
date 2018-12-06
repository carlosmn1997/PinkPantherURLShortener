package urlshortener.team.repository.fixture;

import urlshortener.team.domain.Click;
import urlshortener.team.domain.ShortURL;

public class ClickFixture {

  public static Click click(ShortURL su) {
    return new Click(null, su.getHash(), null, null, null, null, null, null);
  }
}
