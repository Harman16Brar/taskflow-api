package com.taskflow_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Comparator;
import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class TaskflowApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskflowApiApplication.class, args);
    }

}
