package urlshortener.team.domain;

import java.sql.Date;
import java.sql.Timestamp;

public class UriStats {
  private Date creation_date;
  private Long click_number;
  private Timestamp time_last_redirection;

  public UriStats(Date creation_date, Long click_number, Timestamp time_last_redirection) {
    this.creation_date = creation_date;
    this.click_number = click_number;
    this.time_last_redirection = time_last_redirection;
  }

  public Date getCreation_date() {
    return creation_date;
  }

  public Long getClick_number() {
    return click_number;
  }

  public Timestamp getTime_last_redirection() {
    return time_last_redirection;
  }
}
