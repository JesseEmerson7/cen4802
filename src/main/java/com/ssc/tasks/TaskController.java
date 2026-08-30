package com.ssc.tasks;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskStorageService taskStorageService;

    public TaskController(TaskStorageService taskStorageService) {
        this.taskStorageService = taskStorageService;
    }

    @GetMapping("/tasks")
    public List<Task> getTasks() {
        return taskStorageService.loadTasks();
    }

    @PostMapping("/tasks")
    public Task addTask(@RequestBody TaskRequest taskRequest) {
        if (taskRequest == null || taskRequest.getTitle() == null || taskRequest.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task title is required");
        }

        List<Task> tasks = taskStorageService.loadTasks();
        long nextId = tasks.stream().mapToLong(existingTask -> existingTask.getId() == null ? 0 : existingTask.getId()).max().orElse(0L) + 1;

        Task newTask = new Task(nextId, taskRequest.getTitle().trim(), false);
        tasks.add(newTask);
        taskStorageService.saveTasks(tasks);
        return newTask;
    }

    @PutMapping("/tasks/{id}/done")
    public Task markTaskDone(@PathVariable Long id) {
        List<Task> tasks = taskStorageService.loadTasks();

        Task task = tasks.stream()
                .filter(existingTask -> existingTask.getId() != null && existingTask.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));

        task.setDone(true);
        taskStorageService.saveTasks(tasks);
        return task;
    }
}
