package dev.seatlock.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PingController.class)
public class PingControllerTest {
    @Autowired
    private MockMvc mock;

    @Test
    public void returnPongMessage() throws Exception {
        mock.perform(get("/api/v1/ping"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").value("pong")
                );
    }
}
