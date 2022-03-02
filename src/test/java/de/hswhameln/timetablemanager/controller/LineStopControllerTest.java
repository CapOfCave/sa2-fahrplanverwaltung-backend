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
}