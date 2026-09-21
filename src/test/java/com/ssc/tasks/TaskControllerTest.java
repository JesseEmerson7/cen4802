package com.ssc.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskControllerTest {

    private TaskStorageService storageService;
    private TaskController controller;
    private List<Task> tasks;

    // Creates a mocked storage service and controller with an empty task list before each test.
    @BeforeEach
    void setUp() {
        storageService = mock(TaskStorageService.class);
        controller = new TaskController(storageService);
        tasks = new ArrayList<>();
        when(storageService.loadTasks()).thenReturn(tasks);
    }

    // Verifies that a new task receives the next ID and a trimmed title before being saved.
    @Test
    void shouldAddTaskWithNextIdAndTrimmedTitle() {
        tasks.add(new Task(3L, "Existing task", false));

        TaskRequest request = new TaskRequest();
        request.setTitle("  Write tests  ");

        Task result = controller.addTask(request);

        assertEquals(4L, result.getId());
        assertEquals("Write tests", result.getTitle());
        assertFalse(result.isDone());
        verify(storageService).saveTasks(tasks);
    }

    // Verifies that the controller rejects a task request with a blank title.
    @Test
    void shouldRejectMissingTaskTitle() {
        TaskRequest request = new TaskRequest();
        request.setTitle("  ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.addTask(request));

        assertEquals("Task title is required", exception.getMessage());
    }

    // Verifies that an existing task can be marked done and then marked undone.
    @Test
    void shouldMarkTaskDoneAndUndone() {
        Task task = new Task(1L, "Write tests", false);
        tasks.add(task);

        assertTrue(controller.markTaskDone(1L).isDone());

        assertFalse(controller.markTaskUndone(1L).isDone());
        verify(storageService, times(2)).saveTasks(tasks);
    }

    // Verifies that updating a task ID that does not exist throws an exception.
    @Test
    void shouldRejectUnknownTaskId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.markTaskDone(99L));

        assertEquals("Task not found: 99", exception.getMessage());
    }
}
