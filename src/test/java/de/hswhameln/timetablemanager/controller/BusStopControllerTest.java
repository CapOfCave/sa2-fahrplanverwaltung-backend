package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.test.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("As an employee, I can create a bus stop by specifying a unique name.")
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
    @DisplayName("A bus stop cannot be created with a name that is already in use.")
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
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponseCreate));

    }

    @Test
    @DisplayName("As an employee, I can rename bus stops.")
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
    @DisplayName("When trying to rename a nonexistent bus stop a proper exception is thrown.")
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
    @DisplayName("As an employee, I can delete a stop that is not assigned to any bus line.")
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
    @DisplayName("As an employee, I cannot delete a stop that is assigned to a bus line.")
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


    @Test
    @Sql("/data-test.sql")
    void testGetTimetableEmptyResult() throws Exception {

        String expectedResponse = "{}";
        int busStopId = 1;
        this.mockMvc.perform(get("/busstops/{busStopId}/timetable", busStopId)
                        .param("startTime", "2022-03-08T22:04")
                        .param("durationSeconds", "9999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

    }

    @Test
    @Sql("/data-test.sql")
    void testGetTimetable() throws Exception {

        String expectedResponse = """
                {
                    "busStop":{
                      "id":1,
                      "name":"Abbey Road"
                    },
                    "scheduleEntries":[
                      {
                        "schedule":{
                          "id":1,
                          "startTime":"14:35:00",
                          "line":{
                            "id":1,
                            "name":"1"
                          },
                          "finalStop":{
                            "id":5,
                            "name":"East Hills Avenue"
                          }
                        },
                        "arrival":"2022-03-08T14:35:00"
                      }
                    ]
                  }
                """;
        int busStopId = 1;
        this.mockMvc.perform(get("/busstops/{busStopId}/timetable", busStopId)
                        .param("startTime", "2022-03-08T14:00")
                        .param("durationSeconds", Integer.toString(2 * 60 * 60)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

    }

    @Test
    @Sql("/data-test.sql")
    void testGetScheduleForLineLineNonExistent() throws Exception {

        String expectedResponse = "Line with lineId '7777' was not found. Reason: It does not exist.";
        int busStopId = 1;
        int lineId = 7777;
        this.mockMvc.perform(get("/busstops/{busStopId}/schedule/{lineId}", busStopId, lineId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @Sql("/data-test.sql")
    void testGetScheduleForLineBusStopNonExistent() throws Exception {

        String expectedResponse = "BusStop with busStopId '7777' was not found. Reason: It does not exist.";
        int busStopId = 7777;
        int lineId = 1;
        this.mockMvc.perform(get("/busstops/{busStopId}/schedule/{lineId}", busStopId, lineId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));

    }
    @Test
    @Sql("/data-test.sql")
    void testGetSchedulesForLine() throws Exception {

        String expectedResponse = """
                {
                    "busStop":{
                      "id":4,
                      "name":"Dean Avenue"
                    },
                    "scheduleEntries":[
                      {
                        "schedule":{
                          "id":1,
                          "startTime":"14:35:00",
                          "line":{
                            "id":1,
                            "name":"1"
                          },
                          "finalStop":{
                            "id":5,
                            "name":"East Hills Avenue"
                          }
                        },
                        "arrival":"14:36:00"
                      }
                    ]
                  }
                """;
        int busStopId = 4;
        int lineId = 1;
        this.mockMvc.perform(get("/busstops/{busStopId}/schedule/{lineId}", busStopId, lineId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

    }

    @Test
    @DisplayName("As a customer, I can display all bus lines that stop at a specified stop.")
    @Sql("/data-test.sql")
    void testGetBusStop() throws Exception {

        String expectedResponse = """
                {
                    "id": 1,
                    "name": "Abbey Road",
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
                        get("/busstops/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

    }


}