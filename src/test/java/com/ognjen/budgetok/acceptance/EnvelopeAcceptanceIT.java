package com.ognjen.budgetok.acceptance;

import com.microsoft.playwright.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EnvelopeAcceptanceIT {

  private String baseUrl;
  private Navigator navigator;

  @BeforeAll
  void launchBrowser() {
    navigator = new Navigator();
  }

  @AfterAll
  void closeBrowser() {
    navigator.close();
  }

  @BeforeEach
  void createContextAndPage() {
    navigator.initContext();
    baseUrl = "http://localhost:5173";
  }

  @AfterEach
  void closeContext() {
    navigator.closeContext();
  }

  @Test
  void shouldCreateEnvelope() {

    String[] envelope = {"Test Envelope", "100.5"};

    navigator.navigateTo(baseUrl);

    Response response = navigator.submitRequestToCreateEnvelope(envelope, "/api/envelopes", "POST");

    // Verify response status
    verify201ResponseStatus(envelope, response);

  }

  @Test
  void shouldCreateMultipleEnvelopes() {

    String[][] testEnvelopes = createThreeEnvelopes();

    navigator.navigateTo(baseUrl);

    for (String[] envelope : testEnvelopes) {

      Response response = navigator.submitRequestToCreateEnvelope(envelope, "/api/envelopes",
          "POST");

      verify201ResponseStatus(envelope, response);

      navigator.waitForTimeout(500);
    }

    // todo check with Koki how to verify the envelopes creation

    Response getEnvelopesResponse = navigator.sendRequestToGetEnvelopes(baseUrl, "/api/envelopes",
        "GET");

    verifyEnvelopesCreated(getEnvelopesResponse, testEnvelopes);
  }

  private void verify201ResponseStatus(String[] envelope, Response response) {
    String verificationMessage =
        String.format("Expected status 201 (Created) when creating envelope with name '%s'", envelope[0]);
    verifyStatus(201, verificationMessage, response);
  }

  private void verifyEnvelopesCreated(Response apiResponse, String[][] testEnvelopes) {
    // Parse the JSON response
    String responseBody = apiResponse.text();

    // Verify each test envelope exists in the response with correct budget
    for (String[] envelope : testEnvelopes) {

      // Create a pattern to match the envelope in the JSON response
      String envelopePattern = String.format("\"name\"\s*:\s*\"%s\".*?\"budget\"\s*:\s*%s",
          envelope[0], envelope[1]);

      // Check if the envelope with matching name and budget exists
      assertTrue(responseBody.matches("(?s).*" + envelopePattern + ".*"),
          String.format("Expected envelope with name '%s' and budget %s not found in response",
              envelope[0], envelope[1]));
    }

    // Verify each test envelope exists in the response with correct name
    for (String[] envelope : testEnvelopes) {
      assertTrue(
          responseBody.contains(String.format("\"name\":\"%s\"", envelope[0])),
          String.format("Expected envelope with name '%s' not found in response", envelope[0])
      );
    }
  }

  @NotNull
  private String[][] createThreeEnvelopes() {
    return new String[][]{
        {"Rent", "1200.0"},
        {"Groceries", "400"},
        {"Utilities", "200"}
    };
  }

  private void verifyStatus(int expectedStatus, String verificationMessage, Response response) {
    assertEquals(expectedStatus, response.status(), verificationMessage);
  }
}
