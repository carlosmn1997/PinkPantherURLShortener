package urlshortener.team.repository.fixture;

import urlshortener.team.domain.ShortURL;

import java.util.LinkedList;
import java.util.List;

public class ShortURLFixture {

	public static ShortURL url1() {
		return new ShortURL("1", "http://www.unizar.es/", null, null, null, null, null, false,
				null, null, null, null);
	}

	public static ShortURL url1modified() {
		return new ShortURL("1", "http://www.unizar.org/", null, null, null, null, null, false,
				null, null, null, null);
	}

	public static ShortURL url2() {
		return new ShortURL("2", "http://www.unizar.es/", null, null, null, null, null, false,
				null, null, null, null);
	}

	public static ShortURL url3() {
		return new ShortURL("3", "http://www.google.es/", null, null, null, null, null, false,
				null, null, null, null);
	}

	public static ShortURL badUrl() {
		return new ShortURL(null, null, null, null, null, null, null, false,
				null, null, null, null);
	}

	public static ShortURL urlSponsor() {
		return new ShortURL("3", null, null, "sponsor", null, null, null,
				false, null, null, null, null);
	}

	public static ShortURL urlSafe() {
		return new ShortURL("4", null, null, "sponsor", null, null, null, true,
				null, null, null, null);
	}

	public static List<ShortURL> someURLs() {
		ShortURL s = new ShortURL("1", "http://www.unizar.es/", null,
				null, null, null, null, false,
				null, null, true, false);
		List<ShortURL> l = new LinkedList<>();
		for(int i=0; i<3; i++) {
			s.setHash(String.valueOf(i));
			l.add(s);
		}
		s.setHash("-1");
		s.setCheckStatus(false);
		l.add(s);
		return l;
	}
}
