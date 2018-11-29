package urlshortener.team.domain;

import java.sql.Date;
import java.sql.Timestamp;

public class Stats {

    private Long uptime_seconds;
    private Long user_number;
    private Long uri_number;
    private Long click_number;
    private Timestamp time_last_redirection;
    private Long mem_used_mb;
    private Long mem_available_mb;

    public Stats(Long uptime_seconds, Long user_number, Long uri_number, Long click_number,
                 Timestamp time_last_redirection, Long mem_used_mb, Long mem_available_mb) {
        this.uptime_seconds = uptime_seconds;
        this.user_number = user_number;
        this.uri_number = uri_number;
        this.click_number = click_number;
        this.time_last_redirection = time_last_redirection;
        this.mem_used_mb = mem_used_mb;
        this.mem_available_mb = mem_available_mb;
    }

    public Long getUptime_seconds() { return uptime_seconds; }
    public Long getUser_number() { return user_number; }
    public Long getUri_number() { return uri_number; }
    public Long getClick_number() { return click_number; }
    public Timestamp getTime_last_redirection() { return time_last_redirection; }
    public Long getMem_used_mb() { return mem_used_mb; }
    public Long getMem_available_mb() { return mem_available_mb; }
}
