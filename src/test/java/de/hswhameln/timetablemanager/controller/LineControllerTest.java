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
 * Integration test for verifying application behavior regarding use cases 3, 6, 9, and 11
 */
class LineControllerTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("As an employee, I can create a bus line by specifying a unique bus line name.")
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
    @DisplayName("A bus line cannot be created with a name that is already in use.")
    void testCreateLineNameTaken() throws Exception {

        String requestBody = """
                {
                    "name": "S65"
                }
                """;

        String expectedResponseCreate = "Der Wert 'S65' ist ungültig für Argument 'Name'. Grund: Name ist belegt.";

        this.mockMvc.perform(
                        post("/lines/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponseCreate));

    }

    @Test
    @DisplayName("As an employee, I can rename bus lines.")
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

        String expectedResponse = "Buslinie mit ID '7777' konnte nicht gefunden werden. Grund: Sie existiert nicht.";

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
    void testRenameLineNameAlreadyTaken() throws Exception {

        String requestBody = """
                {
                    "name": "S65"
                }
                """;

        String expectedResponse = "Der Wert 'S65' ist ungültig für Argument 'Name'. Grund: Name ist belegt.";


        this.mockMvc.perform(
                        patch("/lines/{lineId}/", 2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @DisplayName("As an employee, I can delete a bus line that is not included in any schedule.")
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
    @DisplayName("As an employee, I cannot delete a bus line that is included in a schedule.")
    @Sql("/data-test.sql")
    void testDeleteLineForbidden() throws Exception {

        String expectedResponse = "Buslinie mit ID 1 konnte nicht gelöscht werden. Grund: Diese Buslinie ist Teil mindestens eines Fahrplans.";
        this.mockMvc.perform(
                        delete("/lines/{lineId}/", 1))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @DisplayName("When trying to delete a nonexistent line a proper exception is thrown.")
    @Sql("/data-test.sql")
    void testDeleteLineLineDoesNotExist() throws Exception {
        int lineId = 7777;
        String expectedResponse = "Buslinie mit ID '7777' konnte nicht gefunden werden. Grund: Sie existiert nicht.";
        this.mockMvc.perform(
                        delete("/lines/{lineId}/", lineId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @DisplayName("As a customer I can display a bus route with all stops including travel times")
    @Sql("/data-test.sql")
    void testGetLine() throws Exception {
        String expectedResponse = """
                {
                   "id": 2,
                   "name": "S65",
                   "lineStops": [
                     {
                       "id": 4,
                       "secondsToNextStop": 60,
                       "busStopId": 1,
                       "busStopName": "Abbey Road"
                     },
                     {
                       "id": 6,
                       "secondsToNextStop": 30,
                       "busStopId": 2,
                       "busStopName": "Barn Street"
                     },
                     {
                       "id": 5,
                       "secondsToNextStop": null,
                       "busStopId": 3,
                       "busStopName": "Camp Street"
                     }
                   ]
                 }
                """;
        this.mockMvc.perform(
                        get("/lines/{id}", 2L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

    }


    @Test
    @Sql("/data-test.sql")
    void testGetLineLineDoesNotExist() throws Exception {
        int lineId = 7777;
        String expectedResponse = "Buslinie mit ID '7777' konnte nicht gefunden werden. Grund: Sie existiert nicht.";
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