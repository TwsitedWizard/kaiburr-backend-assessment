package com.kaiburr.task_api.controller;

import com.kaiburr.task_api.model.Task;
import com.kaiburr.task_api.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    
    @PutMapping("/execute/{id}")
    public ResponseEntity<String> executeTask(@PathVariable String id) {
        try {
            String result = taskService.executeCommand(id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error executing command: " + e.getMessage());
        }
    }

    // ... (keep all the other endpoint methods like createTask, getAllTasks, etc.)
    @PutMapping("/")
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @GetMapping("/")
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        // 1. Call the service to get the task
        Optional<Task> taskOptional = taskService.getTaskById(id);

        // 2. Return the response
        return taskOptional.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/find/by-name/{name}")
    public ResponseEntity<List<Task>> findTasksByName(@PathVariable String name) {
        List<Task> tasks = taskService.findTasksByName(name);
        if (tasks.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}