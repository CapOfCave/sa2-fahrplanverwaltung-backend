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
 * Integration test for verifying application behavior regarding use cases 3, 6, 9, and 11
 */
class LineControllerTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateLine() throws Exception {

        String requestBody = """
                {
                    "name": "S634"
                }
                """;

        String expectedResponse = """
                {
                   "id": 1,
                   "name": "S634",
                   "lineStops": []
                 }
                """;

        this.mockMvc.perform(
                        post("/lines/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

        // verify that result is persisted through multiple calls

        String expectedResponseGet = """
                [
                   {
                     "id": 1,
                     "name": "S634"
                   }
                 ]
                """;
        this.mockMvc.perform(
                        get("/lines/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseGet));

    }

    @Test
    @Sql("/data-test.sql")
    void testCreateLineNameTaken() throws Exception {

        String requestBody = """
                {
                    "name": "S65"
                }
                """;

        String expectedResponseCreate = "The value 'S65' is invalid for argument 'name'. Reason: Name is already taken.";

        this.mockMvc.perform(
                        post("/lines/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().string(expectedResponseCreate));

    }

    @Test
    @Sql("/data-test.sql")
    void testRenameLine() throws Exception {

        String requestBody = """
                {
                    "name": "newName"
                }
                """;

        String expectedResponse = """
                {
                    "id": 1,
                    "name": "newName",
                    "lineStops": [
                      {
                        "id": 1,
                        "secondsToNextStop": 60,
                        "busStopId": 1,
                        "busStopName": "Abbey Road"
                      },
                      {
                        "id": 2,
                        "secondsToNextStop": 120,
                        "busStopId": 4,
                        "busStopName": "Dean Avenue"
                      },
                      {
                        "id": 3,
                        "secondsToNextStop": null,
                        "busStopId": 5,
                        "busStopName": "East Hills Avenue"
                      }
                    ]
                }
                """;

        // verify that rename is persisted

        this.mockMvc.perform(
                        patch("/lines/{lineId}/", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

        String expectedResponseGet = """
                {
                   "id": 1,
                   "name": "newName",
                   "lineStops": [
                     {
                       "id": 1,
                       "secondsToNextStop": 60,
                       "busStopId": 1,
                       "busStopName": "Abbey Road"
                     },
                     {
                       "id": 2,
                       "secondsToNextStop": 120,
                       "busStopId": 4,
                       "busStopName": "Dean Avenue"
                     },
                     {
                       "id": 3,
                       "secondsToNextStop": null,
                       "busStopId": 5,
                       "busStopName": "East Hills Avenue"
                     }
                   ]
                 }
                """;
        this.mockMvc.perform(
                        get("/lines/{lineId}/", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseGet));


    }

    @Test
    @Sql("/data-test.sql")
    void testRenameLineNonexistentLine() throws Exception {

        String requestBody = """
                {
                    "name": "newName"
                }
                """;

        String expectedResponse = "Line with lineId '7777' was not found. Reason: It does not exist.";

        // verify that rename is persisted

        this.mockMvc.perform(
                        patch("/lines/{lineId}/", 7777)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @Sql("/data-test.sql")
    void testDeleteLine() throws Exception {
        int lineId = 2;
        this.mockMvc.perform(
                        delete("/lines/{lineId}/", lineId))
                .andDo(print())
                .andExpect(status().isNoContent());

        this.mockMvc.perform(
                        get("/lines/{lineId}/", lineId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql("/data-test.sql")
    void testDeleteLineForbidden() throws Exception {

        String expectedResponse = "Could not delete Line with id 1. Reason: This Line is part of at least one Schedule.";
        this.mockMvc.perform(
                        delete("/lines/{lineId}/", 1))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @Sql("/data-test.sql")
    void testDeleteLineLineDoesNotExist() throws Exception {
        int lineId = 7777;
        String expectedResponse = "Line with lineId '7777' was not found. Reason: It does not exist.";
        this.mockMvc.perform(
                        delete("/lines/{lineId}/", lineId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @Sql("/data-test.sql")
    void testGetLineLineDoesNotExist() throws Exception {
        int lineId = 7777;
        String expectedResponse = "Line with lineId '7777' was not found. Reason: It does not exist.";
        this.mockMvc.perform(
                        get("/lines/{lineId}/", lineId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    void testGetLinesNoContent() throws Exception {
        String expectedResponse = "[]";
        this.mockMvc.perform(
                        get("/lines/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

}