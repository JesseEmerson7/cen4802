package com.ssc.tasks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class TaskStorageService {

    private final ObjectMapper objectMapper;

    @Value("${task.storage.file:tasks.json}")
    private String storageFile;

    public TaskStorageService() {
        this.objectMapper = new ObjectMapper();
    }

    public synchronized List<Task> loadTasks() {
        Path path = Paths.get(storageFile);

        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try {
            if (Files.size(path) == 0) {
                return new ArrayList<>();
            }

            return objectMapper.readValue(path.toFile(), new TypeReference<List<Task>>() {});
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read tasks from " + storageFile, e);
        }
    }

    public synchronized void saveTasks(List<Task> tasks) {
        Path path = Paths.get(storageFile);
        Path parent = path.getParent();

        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            objectMapper.writeValue(path.toFile(), tasks);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save tasks to " + storageFile, e);
        }
    }
}
