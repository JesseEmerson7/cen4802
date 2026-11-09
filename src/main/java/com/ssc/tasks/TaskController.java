package com.ssc.tasks;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskStorageService taskStorageService;

    public TaskController(TaskStorageService taskStorageService) {
        this.taskStorageService = taskStorageService;
    }

    @GetMapping("/tasks")
    public List<Task> getTasks() {
        List<Task> tasks = taskStorageService.loadTasks();
        logger.info("Listed {} tasks", tasks.size());
        return tasks;
    }

    @PostMapping("/tasks")
    public Task addTask(@RequestBody TaskRequest taskRequest) {
        if (taskRequest == null || taskRequest.getTitle() == null || taskRequest.getTitle().isBlank()) {
            logger.warn("Rejected task creation because the title was blank");
            throw new IllegalArgumentException("Task title is required");
        }

        List<Task> tasks = taskStorageService.loadTasks();
        long nextId = tasks.stream().mapToLong(existingTask -> existingTask.getId() == null ? 0 : existingTask.getId())
                .max().orElse(0L) + 1;

        Task newTask = new Task(nextId, taskRequest.getTitle().trim(), false);
        tasks.add(newTask);
        taskStorageService.saveTasks(tasks);
        logger.info("Created task id={} title='{}'", newTask.getId(), newTask.getTitle());
        return newTask;
    }

    @PutMapping("/tasks/{id}/done")
    public Task markTaskDone(@PathVariable Long id) {
        return updateTaskStatus(id, true);
    }

    @PutMapping("/tasks/{id}/undone")
    public Task markTaskUndone(@PathVariable Long id) {
        return updateTaskStatus(id, false);
    }

    @PutMapping("/tasks/{id}/favorite")
    public Task markTaskFavorite(@PathVariable Long id) {
        return updateTaskFavorite(id, true);
    }

    @PutMapping("/tasks/{id}/unfavorite")
    public Task markTaskUnfavorite(@PathVariable Long id) {
        return updateTaskFavorite(id, false);
    }

    private Task updateTaskFavorite(Long id, boolean favorite) {
        List<Task> tasks = taskStorageService.loadTasks();

        Task task = tasks.stream()
                .filter(existingTask -> existingTask.getId() != null && existingTask.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    logger.warn("Cannot set favorite={} because task id={} was not found", favorite, id);
                    return new IllegalArgumentException("Task not found: " + id);
                });

        task.setFavorite(favorite);
        if (favorite) {
            tasks.remove(task);
            tasks.add(0, task);
        }

        taskStorageService.saveTasks(tasks);
        logger.info("Updated favorite={} for task id={}", favorite, id);
        return task;
    }

    private Task updateTaskStatus(Long id, boolean done) {
        List<Task> tasks = taskStorageService.loadTasks();

        Task task = tasks.stream()
                .filter(existingTask -> existingTask.getId() != null && existingTask.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    logger.warn("Cannot set done={} because task id={} was not found", done, id);
                    return new IllegalArgumentException("Task not found: " + id);
                });

        task.setDone(done);
        taskStorageService.saveTasks(tasks);
        logger.info("Updated done={} for task id={}", done, id);
        return task;
    }
}
