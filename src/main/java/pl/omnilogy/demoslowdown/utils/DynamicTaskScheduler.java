package pl.omnilogy.demoslowdown.utils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import pl.omnilogy.demoslowdown.service.PropertiesService;

import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class DynamicTaskScheduler {
    private ScheduledFuture<?> scheduledTask;
    private final TaskScheduler taskScheduler;
    private final PropertiesService propertiesService;
    private final SyntheticSender syntheticSender;

    @PostConstruct
    public void scheduleTask() {
        log.info("Starting task scheduler with interval: {} ms", propertiesService.getSendInterval());
        scheduledTask = taskScheduler.scheduleAtFixedRate(syntheticSender::send, propertiesService.getSendInterval());
    }

    public void changeRate(long newRate) {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }
        log.info("Changing task scheduler interval to: {} ms", newRate);
        propertiesService.setSendInterval(newRate);
        scheduledTask = taskScheduler.scheduleAtFixedRate(syntheticSender::send, propertiesService.getSendInterval());
    }
}