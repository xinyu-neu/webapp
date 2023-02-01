package com.xinyu.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AppTests {
    @Autowired
    private MockMvc mvc;

    @Test
    public void getHealth() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/healthz").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

