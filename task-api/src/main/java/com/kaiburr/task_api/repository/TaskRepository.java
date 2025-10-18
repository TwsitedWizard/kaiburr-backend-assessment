package com.kaiburr.task_api.repository;

import com.kaiburr.task_api.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {

    // Query method to find tasks by name
    List<Task> findByNameContaining(String name);
}