package com.taskflow_api.activity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityLogListener {
    private final ActivityLogRepository activityLogRepository;
    private final SimpMessagingTemplate messagingTemplate;


    @Async
    @EventListener
    public void onTaskEvent(TaskEvent event) {
        log.info("TaskEvent received: {}", event.getAction().name());
        ActivityLog activityLog = new ActivityLog();
        activityLog.setTaskId(event.getTask().getId());
        activityLog.setUserId(event.getActor().getId());
        activityLog.setAction(event.getAction().name());
        activityLog.setDetail(event.getDetail());

        activityLogRepository.save(activityLog);

        // ADD THIS
        log.info("Sending WebSocket message to /topic/tasks/{}", event.getTask().getId());
        messagingTemplate.convertAndSend(
                "/topic/tasks/" + event.getTask().getId(),
                Map.of(
                        "taskId", event.getTask().getId(),
                        "action", event.getAction().name(),
                        "detail", event.getDetail()
                )
        );
        log.info("WebSocket message sent.");
    }
}
