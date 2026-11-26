package dev.seatlock.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class EventControllerIntegrationTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void returnsEmptyJsonArrayWhenThereAreNoEvents() throws Exception {
        mockMvc.perform(get("/api/v1/events"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("[]", JsonCompareMode.STRICT)
                );
    }

    @Test
    void returnsEventsOrderedByStartTimeThenIdWithUtcTimestamps() throws Exception {
        jdbcTemplate.update("""
                INSERT INTO events (id, name, starts_at) OVERRIDING SYSTEM VALUE
                VALUES
                    (10, 'Later event', TIMESTAMPTZ '2026-12-13 18:00:00+00'),
                    (40, 'Earlier event with larger ID', TIMESTAMPTZ '2026-12-12 20:00:00+02'),
                    (30, 'Earlier event with smaller ID', TIMESTAMPTZ '2026-12-12 19:00:00+01')
                """);

        mockMvc.perform(get("/api/v1/events"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                  {"id": 30, "name": "Earlier event with smaller ID", "startsAt": "2026-12-12T18:00:00Z"},
                                  {"id": 40, "name": "Earlier event with larger ID", "startsAt": "2026-12-12T18:00:00Z"},
                                  {"id": 10, "name": "Later event", "startsAt": "2026-12-13T18:00:00Z"}
                                ]
                                """, JsonCompareMode.STRICT)
                );
    }
}
