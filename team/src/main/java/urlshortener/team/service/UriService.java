package urlshortener.team.service;

public interface UriService {
    boolean checkSyntax(String url);
    boolean checkAlive(String url);
}
