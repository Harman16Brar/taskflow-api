package com.taskflow_api.activity;

import com.taskflow_api.task.Task;
import com.taskflow_api.user.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TaskEvent extends ApplicationEvent {
    private final Task task;
    private final User actor;
    private final TaskAction action;
    private final String detail;

    public TaskEvent(Object source, Task task, User actor, TaskAction action, String detail) {
        super(source);
        this.task = task;
        this.actor = actor;
        this.action = action;
        this.detail = detail;
    }
}
