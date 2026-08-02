package com.schwab.bdd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schwab.infrastructure.persistence.ShortUrlEntityRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class UrlShortenerStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShortUrlEntityRepository repository;

    private MvcResult latestResult;
    private String createdShortCode;

    @Before
    public void clearState() {
        repository.deleteAll();
        latestResult = null;
        createdShortCode = null;
    }

    @Given("the URL shortener API is running")
    public void apiIsRunning() {
        Assertions.assertNotNull(mockMvc);
    }

    @Given("a shortened URL exists for {string}")
    public void aShortenedUrlExistsFor(String longUrl) throws Exception {
        createShortUrl(longUrl);
        assertStatus(201);
    }

    @Given("I create a link for {string}")
    public void iCreateALinkFor(String longUrl) throws Exception {
        createShortUrl(longUrl);
        assertStatus(201);
    }

    @When("I shorten URL {string}")
    public void iShortenUrl(String longUrl) throws Exception {
        createShortUrl(longUrl);
    }

    @When("I resolve the created short code")
    public void iResolveTheCreatedShortCode() throws Exception {
        Assertions.assertNotNull(createdShortCode, "No short code has been created in this scenario");
        latestResult = mockMvc.perform(get("/api/{shortCode}", createdShortCode))
                .andReturn();
    }

    @When("I resolve short code {string}")
    public void iResolveShortCode(String shortCode) throws Exception {
        latestResult = mockMvc.perform(get("/api/{shortCode}", shortCode))
                .andReturn();
    }

    @When("I request analytics")
    public void iRequestAnalytics() throws Exception {
        latestResult = mockMvc.perform(get("/api/analytics"))
                .andReturn();
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int status) {
        assertStatus(status);
    }

    @Then("the response field {string} should equal {string}")
    public void theResponseFieldShouldEqual(String fieldName, String expected) throws Exception {
        JsonNode body = latestBodyAsJson();
        Assertions.assertEquals(expected, body.path(fieldName).asText());
    }

    @Then("the response field {string} should equal number {int}")
    public void theResponseFieldShouldEqualNumber(String fieldName, int expected) throws Exception {
        JsonNode body = latestBodyAsJson();
        Assertions.assertEquals(expected, body.path(fieldName).asInt());
    }

    @Then("the response field {string} should not be empty")
    public void theResponseFieldShouldNotBeEmpty(String fieldName) throws Exception {
        JsonNode body = latestBodyAsJson();
        String value = body.path(fieldName).asText();
        Assertions.assertFalse(value == null || value.isBlank(), "Expected non-empty field: " + fieldName);
    }

    @Then("the response header {string} should equal {string}")
    public void theResponseHeaderShouldEqual(String headerName, String expected) {
        String actual = latestResult.getResponse().getHeader(headerName);
        Assertions.assertEquals(expected, actual);
    }

    @Then("the error code should equal {string}")
    public void theErrorCodeShouldEqual(String expectedCode) throws Exception {
        JsonNode body = latestBodyAsJson();
        Assertions.assertEquals(expectedCode, body.path("code").asText());
    }

    @Then("the error message should contain {string}")
    public void theErrorMessageShouldContain(String snippet) throws Exception {
        JsonNode body = latestBodyAsJson();
        Assertions.assertTrue(body.path("message").asText().contains(snippet));
    }

    private void createShortUrl(String longUrl) throws Exception {
        latestResult = mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"longUrl\":\"" + longUrl + "\"}"))
                .andReturn();

        if (latestResult.getResponse().getStatus() == 201) {
            JsonNode body = latestBodyAsJson();
            createdShortCode = body.path("shortCode").asText();
        }
    }

    private void assertStatus(int expected) {
        Assertions.assertNotNull(latestResult, "No HTTP response captured yet");
        int actual = latestResult.getResponse().getStatus();
        Assertions.assertEquals(expected, actual);
    }

    private JsonNode latestBodyAsJson() throws Exception {
        String content = latestResult.getResponse().getContentAsString();
        return objectMapper.readTree(content);
    }
}
