package urlshortener.team.domain;

public class CsvFormat {
    private String URIOriginal;
    private String URIAcortada;

    public CsvFormat(String URIOriginal, String URIAcortada) {
        this.URIOriginal = URIOriginal;
        this.URIAcortada = URIAcortada;
    }

    public String getURIOriginal() {
        return URIOriginal;
    }

    public void setURIOriginal(String URIOriginal) {
        this.URIOriginal = URIOriginal;
    }

    public String getURIAcortada() {
        return URIAcortada;
    }

    public void setURIAcortada(String URIAcortada) {
        this.URIAcortada = URIAcortada;
    }
}
