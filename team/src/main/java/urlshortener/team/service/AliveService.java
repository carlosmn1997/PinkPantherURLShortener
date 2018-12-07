package urlshortener.team.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface AliveService {
    @Scheduled(cron = "0 3 * * * *")
    public void task();
}
