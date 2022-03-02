package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.IntegrationTest;
import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.repositories.BusStopRepository;
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
 * Integration test for verifying application behavior regarding use cases 2, 6, 10, and 12
 */
class BusStopControllerTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BusStopRepository busStopRepository;


    @Test
    void testCreateBusStop() throws Exception {

        String requestBody = """
                {
                    "name": "BusStopName"
                }
                """;

        String expectedResponse = """
                {
                   "id": 1,
                   "name": "BusStopName",
                   "lines": []
                 }
                """;

        this.mockMvc.perform(
                        post("/busstops/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

        List<BusStop> busStops = this.busStopRepository.findAll();
        assertEquals(1, busStops.size());
        BusStop createdBusStop = busStops.get(0);
        assertEquals("BusStopName", createdBusStop.getName());
        assertEquals(1L, createdBusStop.getId());

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


    }

    @Test
    @Sql("/data-test.sql")
    void testDeleteBusStop() throws Exception {

        this.mockMvc.perform(
                        delete("/busstops/{busStopId}/", 8))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @Sql("/data-test.sql")
    void testGetBusStopDetail() throws Exception {

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
                        get("/busstops/{busStopId}/", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));


    }

}