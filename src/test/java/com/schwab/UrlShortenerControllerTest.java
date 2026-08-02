package com.schwab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UrlShortenerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateShortUrlAndRedirect() throws Exception {
        String response = mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"longUrl\":\"https://example.com/products\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.longUrl").value("https://example.com/products"))
                .andExpect(jsonPath("$.shortCode", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode body = objectMapper.readTree(response);
        String shortCode = body.get("shortCode").asText();

        mockMvc.perform(get("/api/{shortCode}", shortCode))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "https://example.com/products"));
    }

    @Test
    void shouldReturnNotFoundForUnknownShortCode() throws Exception {
        mockMvc.perform(get("/api/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidUrl() throws Exception {
        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"longUrl\":\"not-a-valid-url\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnAnalyticsForCreatedLinks() throws Exception {
        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"longUrl\":\"https://example.com/analytics\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLinks").value(1))
                .andExpect(jsonPath("$.totalRedirects").value(0));
    }

    @Test
    void shouldExposeOpenApiDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }
}
