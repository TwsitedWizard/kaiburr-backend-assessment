package com.kaiburr.task_api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Date;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tasks") // Maps this class to the "tasks" collection in MongoDB
public class Task {

    @Id // Primary Key
    private String id;
    private String name;
    private String owner;
    private String command;
    private List<TaskExecution> taskExecutions;

    // Inner class for TaskExecution
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TaskExecution {
        private Date startTime;
        private Date endTime;
        private String output;
    }
}