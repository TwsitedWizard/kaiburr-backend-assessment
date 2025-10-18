package com.kaiburr.task_api.service;

import com.kaiburr.task_api.model.Task;
import com.kaiburr.task_api.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    
    // Define the allowlist of safe commands
    private static final List<String> ALLOWED_COMMANDS = Arrays.asList("echo", "ls", "date");

    public String executeCommand(String taskId) throws IOException, InterruptedException {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isEmpty()) {
            throw new IllegalArgumentException("Task not found with id: " + taskId);
        }
        
        Task task = taskOptional.get();
        String command = task.getCommand();

        String[] commandParts = command.split("\\s+");
        if (!ALLOWED_COMMANDS.contains(commandParts[0])) {
            return "Error: Command not allowed.";
        }

        // --- SOLUTION START ---
        ProcessBuilder processBuilder;
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

        if (isWindows) {
            // Prepend "cmd.exe /c" to the command for Windows
            List<String> windowsCommand = new ArrayList<>();
            windowsCommand.add("cmd.exe");
            windowsCommand.add("/c");
            windowsCommand.addAll(Arrays.asList(commandParts));
            processBuilder = new ProcessBuilder(windowsCommand);
        } else {
            // Use the command directly for other OS (Linux, macOS)
            processBuilder = new ProcessBuilder(commandParts);
        }
        // --- SOLUTION END ---
        
        Date startTime = new Date();
        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        
        int exitCode = process.waitFor();
        Date endTime = new Date();

        Task.TaskExecution execution = new Task.TaskExecution(startTime, endTime, output.toString());
        
        if (task.getTaskExecutions() == null) {
            task.setTaskExecutions(new ArrayList<>());
        }
        task.getTaskExecutions().add(execution);
        
        taskRepository.save(task);
        
        return "Exit Code: " + exitCode + "\nOutput:\n" + output.toString();
    }
    
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    public List<Task> findTasksByName(String name) {
        return taskRepository.findByNameContaining(name);
    }
    
    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }
}