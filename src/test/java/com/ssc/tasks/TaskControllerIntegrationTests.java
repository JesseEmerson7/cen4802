package com.ssc.tasks;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@TestPropertySource(properties = "task.storage.file=${java.io.tmpdir}/task-manager-test.json")
class TaskControllerIntegrationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        File storageFile = new File(System.getProperty("java.io.tmpdir"), "task-manager-test.json");
        if (storageFile.exists()) {
            storageFile.delete();
        }

        TaskStorageService storageService = webApplicationContext.getBean(TaskStorageService.class);
        Field field = TaskStorageService.class.getDeclaredField("storageFile");
        field.setAccessible(true);
        field.set(storageService, storageFile.getAbsolutePath());
    }

    @Test
    void shouldAddAndCompleteTask() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType("application/json")
                .content("{\"title\":\"Write documentation\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Write documentation"))
                .andExpect(jsonPath("$.done").value(false));

        mockMvc.perform(put("/api/tasks/1/done"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(true));

        mockMvc.perform(put("/api/tasks/1/undone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(false));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Write documentation"))
                .andExpect(jsonPath("$[0].done").value(false));
    }
}
