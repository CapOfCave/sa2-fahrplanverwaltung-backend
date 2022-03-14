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
 * Integration test for verifying application behavior regarding use cases 4 and 7
 */
class LineStopControllerTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("As an employee, I can add existing stops to a bus route in a fixed order " +
            "with travel times to the next stop (the travel times can be set arbitrarily).")
    @Sql("/data-test.sql")
    void testAddBusStop() throws Exception {

        int lineId = 1;
        String requestBody = """
                {
                   "busStopId": 2,
                   "secondsToNextStop": 10,
                   "targetIndex": 2
                 }
                """;

        this.mockMvc.perform(
                        post("/lines/{lineId}/busstops", lineId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());

        // validate persisted state

        String expectedResponseGet = """
                [
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
                      "id": 7,
                      "secondsToNextStop": 10,
                      "busStopId": 2,
                      "busStopName": "Barn Street"
                    },
                    {
                      "id": 3,
                      "secondsToNextStop": null,
                      "busStopId": 5,
                      "busStopName": "East Hills Avenue"
                    }
                  ]
                """;
        this.mockMvc.perform(
                        get("/lines/{lineId}/busstops", lineId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseGet));

    }

    @Test
    @DisplayName("When trying to add a bus stop to a nonexistent line a proper exception is thrown.")
    @Sql("/data-test.sql")
    void testAddBusStopLineNotFound() throws Exception {

        int lineId = 7777;
        String requestBody = """
                {
                   "busStopId": 2,
                   "secondsToNextStop": 10,
                   "targetIndex": 2
                 }
                """;

        String expectedResponse = "Line with lineId '7777' was not found. Reason: It does not exist.";

        this.mockMvc.perform(
                        post("/lines/{lineId}/busstops", lineId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @DisplayName("When trying to add a nonexistent bus stop to a line a proper exception is thrown.")
    @Sql("/data-test.sql")
    void testAddBusStopBusStopNotFound() throws Exception {

        int lineId = 1;
        String requestBody = """
                {
                   "busStopId": 7777,
                   "secondsToNextStop": 10,
                   "targetIndex": 2
                 }
                """;

        String expectedResponse = "BusStop with busStopId '7777' was not found. Reason: It does not exist.";

        this.mockMvc.perform(
                        post("/lines/{lineId}/busstops", lineId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @DisplayName("When trying to add a line with an index that it too high, a proper exception is thrown.")
    @Sql("/data-test.sql")
    void testAddBusStopTargetIndexTooHigh() throws Exception {

        int lineId = 1;
        String requestBody = """
                {
                   "busStopId": 1,
                   "secondsToNextStop": 10,
                   "targetIndex": 7777
                 }
                """;

        String expectedResponse = "The value '7777' is invalid for argument 'targetIndex'. Reason: It must not be greater than the number of stops on this line, which is 3.";

        this.mockMvc.perform(
                        post("/lines/{lineId}/busstops", lineId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @DisplayName("When trying to add a line with a negative target index a proper exception is thrown.")
    @Sql("/data-test.sql")
    void testAddBusStopTargetIndexNegative() throws Exception {

        int lineId = 1;
        String requestBody = """
                {
                   "busStopId": 1,
                   "secondsToNextStop": 10,
                   "targetIndex": -1
                 }
                """;

        String expectedResponse = "The value '-1' is invalid for argument 'targetIndex'. Reason: It must be greater than 0.";

        this.mockMvc.perform(
                        post("/lines/{lineId}/busstops", lineId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @DisplayName("As an employee, I can remove stops from a bus line. Linked schedules may need to be adjusted.")
    @Sql("/data-test.sql")
    void testRemoveBusStop() throws Exception {

        int lineId = 1;
        int lineStopId = 2;

        this.mockMvc.perform(
                        delete("/lines/{lineId}/busstops/{lineStopId}", lineId, lineStopId))
                .andDo(print())
                .andExpect(status().isOk());

        // validate persisted state

        String expectedResponseGet = """
                [
                    {
                      "id": 1,
                      "secondsToNextStop": 60,
                      "busStopId": 1,
                      "busStopName": "Abbey Road"
                    },
                    {
                      "id": 3,
                      "secondsToNextStop": null,
                      "busStopId": 5,
                      "busStopName": "East Hills Avenue"
                    }
                  ]
                """;
        this.mockMvc.perform(
                        get("/lines/{lineId}/busstops", lineId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseGet));

    }

    @Test
    @DisplayName("When trying to remove a bus stop from a nonexistent line, a proper exception is thrown.")
    @Sql("/data-test.sql")
    void testRemoveBusStopLineNotFound() throws Exception {

        int lineId = 7777;
        int lineStopId = 2;

        String expectedResponse = "Line with lineId '7777' was not found. Reason: It does not exist.";

        this.mockMvc.perform(
                        delete("/lines/{lineId}/busstops/{lineStopId}", lineId, lineStopId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @DisplayName("When trying to remove a nonexistent bus stop from a line a proper exception is thrown.")
    @Sql("/data-test.sql")
    void testRemoveBusStopLineStopNotFound() throws Exception {

        int lineId = 1;
        int lineStopId = 7777;

        String expectedResponse = "LineStop with lineStopId '7777' was not found. Reason: It does not exist.";

        this.mockMvc.perform(
                        delete("/lines/{lineId}/busstops/{lineStopId}", lineId, lineStopId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @DisplayName("When trying to remove a bus stop from a line which does not stop there a proper exception is thrown.")
    @Sql("/data-test.sql")
    void testRemoveBusStopLineStopNotOnThisLine() throws Exception {

        int lineId = 1;
        int lineStopId = 4;

        String expectedResponse = "LineStop with lineStopId '4' was not found. Reason: It does not exist on line 1, but on line 2.";

        this.mockMvc.perform(
                        delete("/lines/{lineId}/busstops/{lineStopId}", lineId, lineStopId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @DisplayName("When trying to get information about the stops of a nonexistent line a proper exception is thrown.")
    void testGetBusStopsLineNotFound() throws Exception {

        int lineId = 7777;

        String expectedResponse = "Line with lineId '7777' was not found. Reason: It does not exist.";

        this.mockMvc.perform(
                        get("/lines/{lineId}/busstops/", lineId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @DisplayName("As an employee, I can change modify a line stop's time to next stop and position")
    @Sql("/data-test.sql")
    void testModifyBusStop() throws Exception {

        int lineId = 1;
        long lineStopId = 1;

        String requestBody = """
                {
                   "secondsToNextStop": 747,
                   "targetIndex": 2
                 }
                """;


        String expectedResponse = """
                {
                    "id": 1,
                    "name": "1",
                    "lineStops": [
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
                      },
                      {
                        "id": 1,
                        "secondsToNextStop": 747,
                        "busStopId": 1,
                        "busStopName": "Abbey Road"
                      }
                    ]
                }
                """;
        this.mockMvc.perform(
                        patch("/lines/{lineId}/busstops/{lineStopId}", lineId, lineStopId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

    }

    @Test
    @DisplayName("When trying to move a line to a negative target index a proper exception is thrown.")
    @Sql("/data-test.sql")
    void testModifyBusStopTargetIndexNegative() throws Exception {

        int lineId = 1;
        long lineStopId = 1;

        String requestBody = """
                {
                   "secondsToNextStop": 10,
                   "targetIndex": -1
                 }
                """;

        String expectedResponse = "The value '-1' is invalid for argument 'targetIndex'. Reason: It must be greater than 0.";

        this.mockMvc.perform(
                        patch("/lines/{lineId}/busstops/{lineStopId}", lineId, lineStopId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @DisplayName("When trying to move a line to a target index that it too high, a proper exception is thrown.")
    @Sql("/data-test.sql")
    void testModifyBusStopTargetIndexTooHigh() throws Exception {

        int lineId = 1;
        long lineStopId = 1;
        String requestBody = """
                {
                   "busStopId": 1,
                   "secondsToNextStop": 10,
                   "targetIndex": 7777
                 }
                """;

        String expectedResponse = "The value '7777' is invalid for argument 'targetIndex'. Reason: It must be strictly smaller than the number of stops on this line, which is 3.";

        this.mockMvc.perform(
                        patch("/lines/{lineId}/busstops/{lineStopId}", lineId, lineStopId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));

    }


}