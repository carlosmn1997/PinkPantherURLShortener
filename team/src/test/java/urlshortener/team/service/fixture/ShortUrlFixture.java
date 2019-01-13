package urlshortener.team.service.fixture;

import urlshortener.team.domain.ShortURL;

public class ShortUrlFixture {

    public static ShortURL reachableAndLastCheckFalse() {
        ShortURL r = new ShortURL();
        r.setHash("1");
        r.setCheckStatus(true);
        r.setAliveOnLastCheck(false);
        r.setTarget("https://google.com");
        return r;
    }

    public static ShortURL reachableAndLastCheckTrue() {
        ShortURL r = new ShortURL();
        r.setHash("2");
        r.setCheckStatus(true);
        r.setAliveOnLastCheck(true);
        r.setTarget("https://google.com");
        return r;
    }

    public static ShortURL unreachableAndLastCheckTrue() {
        ShortURL r = new ShortURL();
        r.setHash("3");
        r.setCheckStatus(true);
        r.setAliveOnLastCheck(true);
        r.setTarget("https://mipaginanoexisteahora.com");
        return r;
    }

    public static ShortURL unreachableAndLastCheckFalse() {
        ShortURL r = new ShortURL();
        r.setHash("4");
        r.setCheckStatus(true);
        r.setAliveOnLastCheck(false);
        r.setTarget("https://mipaginanoexisteahora.com");
        return r;
    }

    public static ShortURL reachableAndValidSponsor(){
        ShortURL r = new ShortURL();
        r.setHash("5");
        r.setTarget("https://www.google.es/");
        r.setSponsor("http://webdiis.unizar.es/asignaturas/SL/");
        return r;
    }

    public static ShortURL reachableAndInvalidSponsor(){
        ShortURL r = new ShortURL();
        r.setHash("6");
        r.setTarget("https://www.google.es/");
        r.setSponsor("https://mipaginanoexisteahora.com");
        return r;
    }
}
