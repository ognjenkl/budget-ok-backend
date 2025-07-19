package com.ognjen.budgetok.acceptance;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Response;
import com.ognjen.budgetok.application.Envelope;
import java.util.List;
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
  void shouldAllowSubmittingEnvelopeForm() {

    navigator.navigateTo(baseUrl + "/login");

    navigator.instantiateInputFields();

    Envelope envelope = Envelope.builder()
        .name("Test Envelope")
        .budget(100.50)
        .build();

    Response response = navigator.sendRequestToCreateEnvelope(envelope);
//    Response response = sendRequestToCreateEnvelope(envelope, nameInput, budgetInput, submitButton);

    verifyEnvelopeCreated(envelope, response);
  }

//  private Response getResponse(String path, String method, Runnable callback) {
//
//    // Set up response waiting before clicking the button
//    return page.waitForResponse(
//        response -> isPreconditionStisfied(path, method, response),
//        callback
//    );
//  }

//  private boolean isPreconditionStisfied(String path, String method, Response response) {
//    return response.url().endsWith(path) && response.request().method().equals(method);
//  }

//  private void navigateTo(String url) {
////     Navigate to the login page
//    page.navigate(url);
//  }

  @Test
  void shouldCreateMultipleEnvelopes() {

    // Test data
    List<Envelope> testEnvelopes = createThreeEnvelopes();

    // Navigate to the login page
    navigator.navigateTo(baseUrl + "/login");

//    // Locate form elements
//    // Locate form element
//    Locator nameInput = page.locator("input[name='name']");
//    // Locate form element
//    Locator budgetInput = page.locator("input[name='budget']");
//    // Locate form element
//    Locator submitButton = page.locator("button:has-text('Save Envelope')");

    // Create each envelope
    for (Envelope envelope : testEnvelopes) {

//      Response response = sendRequestToCreateEnvelope(envelope, nameInput, budgetInput,
//          submitButton);
      Response response = navigator.sendRequestToCreateEnvelope(envelope);

      verifyEnvelopeCreated(envelope, response);

      navigator.waitForTimeout(500);
    }

    // Get the list of all envelopes from the API
    Response apiResponse = navigator.getResponse("/api/envelopes", "GET",
        () -> navigator.navigateTo(baseUrl + "/api/envelopes"));

    // Parse the JSON response
    String responseBody = apiResponse.text();

    // Verify each test envelope exists in the response with correct budget
    for (Envelope envelope : testEnvelopes) {

      // Create a pattern to match the envelope in the JSON response
      String envelopePattern = String.format("\"name\"\s*:\s*\"%s\".*?\"budget\"\s*:\s*%s",
          envelope.getName(), envelope.getBudget());

      // Check if the envelope with matching name and budget exists
      assertTrue(responseBody.matches("(?s).*" + envelopePattern + ".*"),
          String.format("Expected envelope with name '%s' and budget %s not found in response",
              envelope.getName(), envelope.getBudget()));
    }

    // Verify each test envelope exists in the response with correct name
    for (Envelope envelope : testEnvelopes) {
      assertTrue(
          responseBody.contains(String.format("\"name\":\"%s\"", envelope.getName())),
          String.format("Expected envelope with name '%s' not found in response",
              envelope.getName())
      );
    }
  }

  @NotNull
  private List<Envelope> createThreeEnvelopes() {
    return List.of(
        Envelope.builder().name("Rent").budget(1200.0).build(),
        Envelope.builder().name("Groceries").budget(400).build(),
        Envelope.builder().name("Utilities").budget(200).build());
  }

  private void verifyEnvelopeCreated(Envelope envelope, Response response) {

    // Verify response status
    String verificationMessage =
        "API should return 201 Created status for envelope: " + envelope.getName();
    verifyStatus(201, verificationMessage, response);

    // Parse and verify response
    String responseText = response.text();
    assertTrue(responseText.contains("\"id\""), "Response should contain id field");
    assertTrue(responseText.contains("\"name\""), "Response should contain name field");
    assertTrue(responseText.contains("\"budget\""), "Response should contain budget field");

    assertTrue(responseText.contains(String.format("\"name\":\"%s\"", envelope.getName())),
        String.format("Response should contain the submitted name: %s", envelope.getName()));
    assertTrue(responseText.contains(String.format("\"budget\":%s", envelope.getBudget())),
        String.format("Response should contain the submitted budget: %s", envelope.getBudget()));
  }


//  private Response sendRequestToCreateEnvelope(Envelope envelope, Locator nameInput,
//      Locator budgetInput,
//      Locator submitButton) {
//
//    // Fill in the form
//    nameInput.fill(envelope.getName());
//    budgetInput.fill(envelope.getBudget() + "");
//
//    // Submit the form and wait for response
//    return getResponse("/api/envelopes", "POST", submitButton::click);
//  }

  private void verifyStatus(int expectedStatus, String verificationMessage, Response response) {
    assertEquals(expectedStatus, response.status(), verificationMessage);
  }
}
