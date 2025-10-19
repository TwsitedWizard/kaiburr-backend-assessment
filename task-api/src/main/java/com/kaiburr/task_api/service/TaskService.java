package com.kaiburr.task_api.service;

import com.kaiburr.task_api.model.Task;
import com.kaiburr.task_api.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;

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

    public String executeCommand(String taskId) throws IOException, InterruptedException, ApiException {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isEmpty()) {
            throw new IllegalArgumentException("Task not found with id: " + taskId);
        }
        
        Task task = taskOptional.get();
        String command = task.getCommand();
        String[] commandParts = command.split("\\s+");

        // --- New Kubernetes Logic ---
        ApiClient client = ClientBuilder.cluster().build(); // Automatically uses in-cluster config
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();

        String podName = "task-exec-" + taskId + "-" + System.currentTimeMillis();

        // // Define the Pod to be created
        // final V1Pod pod = new V1PodBuilder()
        //     .withNewMetadata()
        //         .withName(podName)
        //     .endMetadata()
        //     .withNewSpec()
        //         .withContainers(new V1ContainerBuilder()
        //             .withName("task-runner")
        //             .withImage("busybox") // A small, simple image
        //             .withCommand(commandParts[0])
        //             .withArgs(Arrays.copyOfRange(commandParts, 1, commandParts.length))
        //             .build())
        //         .withRestartPolicy("Never") // Run once and then stop
        //     .endSpec()
        //     .build();

        V1Container container = new V1Container();
        container.setName("task-runner");
        container.setImage("busybox");
        container.setCommand(Arrays.asList(commandParts[0]));
        if (commandParts.length > 1) {
            container.setArgs(Arrays.asList(Arrays.copyOfRange(commandParts, 1, commandParts.length)));
        }

        V1PodSpec spec = new V1PodSpec();
        spec.setContainers(Arrays.asList(container));
        spec.setRestartPolicy("Never");

        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(podName);

        V1Pod pod = new V1Pod();
        pod.setMetadata(metadata);
        pod.setSpec(spec);

        Date startTime = new Date();
        api.createNamespacedPod("default", pod, null, null, null, null);

        // Wait for the Pod to complete
        try {
            while (true) {
                V1Pod createdPod = api.readNamespacedPod(podName, "default", null);
                String phase = createdPod.getStatus().getPhase();
                if ("Succeeded".equals(phase) || "Failed".equals(phase)) {
                    break;
                }
                TimeUnit.SECONDS.sleep(2);
            }
        } catch (Exception e) {
            // Clean up pod on failure
            api.deleteNamespacedPod(podName, "default", null, null, null, null, null, null);
            throw e;
        }

        // Retrieve the logs (output) from the Pod
        String podLogs = api.readNamespacedPodLog(podName, "default", null, null, null, null, null, null, null, null, null);
        Date endTime = new Date();
        
        // Crucially, clean up the pod after getting logs
        api.deleteNamespacedPod(podName, "default", null, null, null, null, null, null);

        // Create and store the execution record
        Task.TaskExecution execution = new Task.TaskExecution(startTime, endTime, podLogs);
        if (task.getTaskExecutions() == null) {
            task.setTaskExecutions(new ArrayList<>());
        }
        task.getTaskExecutions().add(execution);
        taskRepository.save(task);
        
        return "Pod executed. Output:\n" + podLogs;
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