package urlshortener.team.stompMessages;

public class ReadyMessage {

    private String content;
    private String idTimer;

    public ReadyMessage () {}

    public ReadyMessage(String ready) {
        this.content = ready;
    }

    public String getContent() {
        return content;
    }

    public void setReady(String ready) {
        this.content = ready;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdTimer() {
        return idTimer;
    }

    public void setIdTimer(String idTimer) {
        this.idTimer = idTimer;
    }
}
