package urlshortener.team.stompMessages;

public class ReadyMessage {

    private String content; // The value dont care, only the arrival of the message

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
}
