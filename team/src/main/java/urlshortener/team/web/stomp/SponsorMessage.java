package urlshortener.team.web.stomp;

public class SponsorMessage {

  private String sponsor;

  public SponsorMessage() {
  }

  public SponsorMessage(String sponsor) {
    this.sponsor = sponsor;
  }

  public String getSponsor() {
    return sponsor;
  }

  public void setSponsor(String sponsor) {
    this.sponsor = sponsor;
  }
}
