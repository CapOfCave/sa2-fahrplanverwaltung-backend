package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.repositories.ScheduleRepository;
import de.hswhameln.timetablemanager.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for verifying application behavior regarding use cases 5 and 8
 */
class ScheduleControllerTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    @Sql("/data-without-schedules.sql")
    void testCreateSchedule() throws Exception {

        String requestBody = """
                {
                   "lineId": 1,
                   "startTime": "14:35:00",
                   "reverseDirection": false
                }
                """;

        String expectedResponseCreate = """
                {
                    "id": 1,
                    "startTime": "14:35:00",
                    "line": {
                      "id": 1,
                      "name": "S65"
                    },
                    "finalStop": {
                      "id": 3,
                      "name": "Camp Street"
                    }
                }
                """;

        this.mockMvc.perform(
                        post("/schedules/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseCreate));

        // verify that /create actually created the schedule

        String expectedResponseGet = """
                [
                   {
                     "id": 1,
                     "startTime": "14:35:00",
                     "line": {
                       "id": 1,
                       "name": "S65"
                     },
                     "finalStop": {
                       "id": 3,
                       "name": "Camp Street"
                    }
                   }
                 ]
                """;

        this.mockMvc.perform(
                        get("/schedules/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseGet));
    }

    @Test
    @Sql("/data-test.sql")
    void testDeleteSchedule() throws Exception {
        this.mockMvc.perform(
                        delete("/schedules/{scheduleId}", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(0, this.scheduleRepository.count());
    }

}