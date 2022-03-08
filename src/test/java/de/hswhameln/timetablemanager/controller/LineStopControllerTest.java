package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void testGetBusStopsLineNotFound() throws Exception {

        int lineId = 7777;

        String expectedResponse = "Line with lineId '7777' was not found. Reason: It does not exist.";

        this.mockMvc.perform(
                        get("/lines/{lineId}/busstops/", lineId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));

    }


}