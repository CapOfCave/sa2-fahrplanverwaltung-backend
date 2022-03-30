package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.repositories.ScheduleRepository;
import de.hswhameln.timetablemanager.test.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    @DisplayName("As an employee, I can add bus lines to a schedule in the following form: " +
            "Bus line X starts at Y o'clock in the direction of terminal stop Z.")
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

        this.mockMvc.perform(get("/schedules/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseGet));
    }

    @Test
    @DisplayName("When trying to create a schedule with an invalid lineId a proper exception is thrown")
    @Sql("/data-test.sql")
    void testCreatingScheduleLineNotFound() throws Exception {

        String requestBody = """
                {
                   "lineId": 7777,
                   "startTime": "14:35:00",
                   "reverseDirection": false
                }
                """;

        String expectedResponse = "Buslinie mit ID '7777' konnte nicht gefunden werden. Grund: Sie existiert nicht.";
        this.mockMvc.perform(
                        post("/schedules/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @DisplayName("As an employee I can delete a schedule.")
    @Sql("/data-test.sql")
    void testDeleteSchedule() throws Exception {
        this.mockMvc.perform(
                        delete("/schedules/{scheduleId}", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(0, this.scheduleRepository.count());
    }

    @Test
    @DisplayName("When trying to delete a schedule with an invalid scheduleId a proper exception is thrown")
    @Sql("/data-test.sql")
    void testDeleteScheduleNotFound() throws Exception {
        String expectedResponse = "Fahrplan mit ID '7777' konnte nicht gefunden werden. Grund: Er existiert nicht.";
        this.mockMvc.perform(
                        delete("/schedules/{scheduleId}", 7777L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));

        assertEquals(1, this.scheduleRepository.count());
    }

    @Test
    @DisplayName("As an employee, I can modify a schedule's direction and start time.")
    @Sql("/data-test.sql")
    void testModifySchedule() throws Exception {

        String requestBody = """
                {
                   "startTime": "14:38:00",
                   "reverseDirection": true
                }
                """;

        String expectedResponseCreate = """
                {
                    "id": 1,
                    "startTime": "14:38:00",
                    "line": {
                      "id": 1,
                      "name": "1"
                    },
                    "finalStop": {
                      "id": 1,
                      "name": "Abbey Road"
                    }
                }
                """;

        this.mockMvc.perform(
                        patch("/schedules/{scheduleId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseCreate));

    }

    @Test
    @DisplayName("When trying to modify a schedule with an invalid scheduleId a proper exception is thrown")
    @Sql("/data-test.sql")
    void testModifyScheduleNotFound() throws Exception {

        String requestBody = """
                {
                   "startTime": "14:38:00",
                   "reverseDirection": true
                }
                """;
        String expectedResponse = "Fahrplan mit ID '7777' konnte nicht gefunden werden. Grund: Er existiert nicht.";
        this.mockMvc.perform(
                        patch("/schedules/{scheduleId}", 7777L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));
    }

}