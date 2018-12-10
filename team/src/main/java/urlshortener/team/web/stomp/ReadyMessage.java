package urlshortener.team.web.stomp;

public class ReadyMessage {

  private String content;
  private String idTimer;

  public ReadyMessage() {
  }

  public ReadyMessage(String ready) {
    this.content = ready;
  }

  public ReadyMessage(String content, String timer){ this.content = content; this.idTimer = timer; }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setReady(String ready) {
    this.content = ready;
  }

  public String getIdTimer() {
    return idTimer;
  }

  public void setIdTimer(String idTimer) {
    this.idTimer = idTimer;
  }
}
