package urlshortener.team.web.fixture;

import urlshortener.team.domain.ShortURL;

public class QRFixture {

    public static ShortURL someUrlwithQr() {
        return new ShortURL("someKey", "http://example.com/", null, null, null,
                null, 307, true, null, null, null, null,true,null);
    }

    public static ShortURL someUrlwithNoQr() {
        return new ShortURL("someKey", "http://example.com/", null, null, null,
                null, 307, true, null, null, null, null,false,null);
    }

    public static ShortURL someUrlwithQrProgress() {
        return new ShortURL("someKey", "http://example.com/", null, null, null,
                null, 307, true, null, null, null, null,true,null);
    }
}
