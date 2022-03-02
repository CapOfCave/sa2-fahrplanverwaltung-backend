package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.IntegrationTest;
import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.repositories.LineRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Autowired
    private LineRepository lineRepository;


    @Test
    void testCreateLine() throws Exception {

        String requestBody = """
                {
                    "name": "LineName"
                }
                """;

        String expectedResponse = """
                {
                   "id": 1,
                   "name": "LineName",
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

        List<Line> lines = this.lineRepository.findAll();
        assertEquals(1, lines.size());
        Line createdLine = lines.get(0);
        assertEquals("LineName", createdLine.getName());
        assertEquals(1L, createdLine.getId());

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
                        "index": 0,
                        "secondsToNextStop": 60,
                        "busStopId": 1,
                        "busStopName": "Abbey Road"
                      },
                      {
                        "id": 2,
                        "index": 2,
                        "secondsToNextStop": 120,
                        "busStopId": 4,
                        "busStopName": "Dean Avenue"
                      },
                      {
                        "id": 3,
                        "index": 3,
                        "secondsToNextStop": null,
                        "busStopId": 5,
                        "busStopName": "East Hills Avenue"
                      }
                    ]
                }
                """;

        this.mockMvc.perform(
                        patch("/lines/{lineId}/", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));


    }

    @Test
    @Sql("/data-test.sql")
    void testDeleteLine() throws Exception {
        this.mockMvc.perform(
                        delete("/lines/{lineId}/", 2))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @Sql("/data-test.sql")
    void testGetLineDetail() throws Exception {

        String expectedResponse = """
                {
                   "id": 1,
                   "name": "1",
                   "lineStops": [
                     {
                       "id": 1,
                       "index": 0,
                       "secondsToNextStop": 60,
                       "busStopId": 1,
                       "busStopName": "Abbey Road"
                     },
                     {
                       "id": 2,
                       "index": 2,
                       "secondsToNextStop": 120,
                       "busStopId": 4,
                       "busStopName": "Dean Avenue"
                     },
                     {
                       "id": 3,
                       "index": 3,
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
                .andExpect(content().json(expectedResponse));


    }
}