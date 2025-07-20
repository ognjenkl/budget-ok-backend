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
    baseUrl = "http://localhost:8080";
  }

  @AfterEach
  void closeContext() {
    navigator.closeContext();
  }

  @Test
  void shouldCreateEnvelope() {

    String[] envelope = {"Test Envelope", "100.5"};

    navigator.navigateTo(baseUrl + "/login");

    Response response = navigator.sendRequestToCreateEnvelope(envelope, "/api/envelopes", "POST");

    verifyEnvelopeCreated(envelope, response);
  }

  @Test
  void shouldCreateMultipleEnvelopes() {

    String[][] testEnvelopes = createThreeEnvelopes();

    navigator.navigateTo(baseUrl + "/login");

    for (String[] envelope : testEnvelopes) {

      Response response = navigator.sendRequestToCreateEnvelope(envelope, "/api/envelopes", "POST");

      verifyEnvelopeCreated(envelope, response);

      navigator.waitForTimeout(500);
    }

    Response getEnvelopesResponse = navigator.sendRequestToGetEnvelopes(baseUrl, "/api/envelopes", "GET");

    verifyEnvelopesCreated(getEnvelopesResponse, testEnvelopes);
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

  private void verifyEnvelopeCreated(String[] envelope, Response response) {

    // Verify response status
    String verificationMessage =
        "API should return 201 Created status for envelope: " + envelope[0];
    verifyStatus(201, verificationMessage, response);

    // Parse and verify response
    String responseText = response.text();
    assertTrue(responseText.contains("\"id\""), "Response should contain id field");
    assertTrue(responseText.contains("\"name\""), "Response should contain name field");
    assertTrue(responseText.contains("\"budget\""), "Response should contain budget field");

    assertTrue(responseText.contains(String.format("\"name\":\"%s\"", envelope[0])),
        String.format("Response should contain the submitted name: %s", envelope[0]));
    assertTrue(responseText.contains(String.format("\"budget\":%s", envelope[1])),
        String.format("Response should contain the submitted budget: %s", envelope[1]));
  }

  private void verifyStatus(int expectedStatus, String verificationMessage, Response response) {
    assertEquals(expectedStatus, response.status(), verificationMessage);
  }
}
