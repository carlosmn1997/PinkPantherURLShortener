package urlshortener.team.web.fixture;

import urlshortener.team.domain.ShortURL;

public class ShortURLFixture {

	public static ShortURL someUrl() {
		return new ShortURL("someKey", "http://example.com/", null, null, null,
				null, 307, true, null, null);
	}

	public static ShortURL someUrlWithSponsor() {
		return new ShortURL("someKey", "http://example.com/", null, "http://pinkpanther.com/", null,
				null, 307, true, null, null);
	}
}
