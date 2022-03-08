package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for verifying application behavior regarding use cases 2, 6, 10, and 12
 */
class BusStopControllerTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateBusStop() throws Exception {

        String requestBody = """
                {
                    "name": "Abbey Road"
                }
                """;

        String expectedResponseCreate = """
                {
                   "id": 1,
                   "name": "Abbey Road",
                   "lines": []
                }
                """;

        this.mockMvc.perform(
                        post("/busstops/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseCreate));

        String expectedResponseGet = """
                [
                   {
                     "id": 1,
                     "name": "Abbey Road"
                   }
                 ]
                """;
        this.mockMvc.perform(
                        get("/busstops"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseGet));

    }

    @Test
    @Sql("/data-test.sql")
    void testCreateBusStopAlreadyExists() throws Exception {

        String requestBody = """
                {
                    "name": "Abbey Road"
                }
                """;

        String expectedResponseCreate = "The value 'Abbey Road' is invalid for argument 'name'. Reason: Name is already taken.";

        this.mockMvc.perform(
                        post("/busstops/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().string(expectedResponseCreate));

    }

    @Test
    @Sql("/data-test.sql")
    void testRenameBusStop() throws Exception {

        String requestBody = """
                {
                    "name": "newName"
                }
                """;

        String expectedResponse = """
                {
                   "id": 1,
                   "name": "newName",
                   "lines": [
                     {
                       "id": 1,
                       "name": "1"
                     },
                     {
                       "id": 2,
                       "name": "S65"
                     }
                   ]
                 }
                """;

        this.mockMvc.perform(
                        patch("/busstops/{busStopId}/", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));


        // verify that result is persisted through multiple calls
        this.mockMvc.perform(
                        get("/busstops/{busStopId}/", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

    }

    @Test
    @Sql("/data-test.sql")
    void testRenameBusStopNonExistent() throws Exception {

        String requestBody = """
                {
                    "name": "newName"
                }
                """;

        String expectedResponse = "BusStop with busStopId '7777' was not found. Reason: It does not exist.";

        this.mockMvc.perform(
                        patch("/busstops/{busStopId}/", 7777)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @Sql("/data-test.sql")
    void testDeleteBusStop() throws Exception {

        int busStopId = 8;
        this.mockMvc.perform(
                        delete("/busstops/{busStopId}/", busStopId))
                .andDo(print())
                .andExpect(status().isOk());

        // verify that result is actually deleted
        this.mockMvc.perform(
                        get("/busstops/{busStopId}/", busStopId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql("/data-test.sql")
    void testDeleteBusStopForbidden() throws Exception {

        String expectedResponse = "Could not delete BusStop with id 1. Reason: This BusStop is part of at least one line.";
        this.mockMvc.perform(
                        delete("/busstops/{busStopId}/", 1))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @Sql("/data-test.sql")
    void testDeleteBusStopNonExistent() throws Exception {

        String expectedResponse = "BusStop with busStopId '7777' was not found. Reason: It does not exist.";
        int busStopId = 7777;
        this.mockMvc.perform(
                        delete("/busstops/{busStopId}/", busStopId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @Sql("/data-test.sql")
    void testGetBusStopNonExistent() throws Exception {

        String expectedResponse = "BusStop with busStopId '7777' was not found. Reason: It does not exist.";
        int busStopId = 7777;
        this.mockMvc.perform(get("/busstops/{busStopId}/", busStopId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @Sql("/data-test.sql")
    void testGetBusStopScheduleNonExistent() throws Exception {

        String expectedResponse = "BusStop with busStopId '7777' was not found. Reason: It does not exist.";
        int busStopId = 7777;
        this.mockMvc.perform(get("/busstops/{busStopId}/schedule", busStopId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @Sql("/data-test.sql")
    void testGetTimetableNonExistent() throws Exception {

        String expectedResponse = "BusStop with busStopId '7777' was not found. Reason: It does not exist.";
        int busStopId = 7777;
        this.mockMvc.perform(get("/busstops/{busStopId}/timetable", busStopId)
                        .param("startTime", "2022-03-08T22:04")
                        .param("durationSeconds", "9999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));

    }

}