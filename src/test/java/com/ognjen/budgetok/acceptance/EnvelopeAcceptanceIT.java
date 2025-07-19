package com.ognjen.budgetok.acceptance;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EnvelopeAcceptanceIT {

  @LocalServerPort
  private int port;

  private Playwright playwright;
  private Browser browser;
  private BrowserContext context;
  private Page page;
  private String baseUrl;

  @BeforeAll
  void launchBrowser() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
        .setHeadless(false) // Set to true in CI environment
        .setSlowMo(50));
  }

  @AfterAll
  void closeBrowser() {
    if (playwright != null) {
      playwright.close();
    }
  }

  @BeforeEach
  void createContextAndPage() {
    context = browser.newContext();
    page = context.newPage();
    baseUrl = "http://localhost:8080";
  }

  @AfterEach
  void closeContext() {
    if (context != null) {
      context.close();
    }
  }

  @Test
  void shouldAllowSubmittingEnvelopeForm() {
    // Navigate to the login page
    page.navigate(baseUrl + "/login");

    // Locate form elements
    var nameInput = page.locator("input[name='name']");
    var budgetInput = page.locator("input[name='budget']");
    var submitButton = page.locator("button:has-text('Save Envelope')");

    // Fill in the form
    nameInput.fill("Test Envelope");
    budgetInput.fill("100.50");

    // Set up response waiting before clicking the button
    Response response = page.waitForResponse(
        response1 -> response1.url().endsWith("/api/envelopes") &&
            response1.request().method().equals("POST"),
        submitButton::click
    );

    // Verify response status
    assertEquals(201, response.status(), "API should return 201 Created status");

    // Parse and verify response
    String responseText = response.text();
    assertTrue(responseText.contains("\"id\""), "Response should contain id field");
    assertTrue(responseText.contains("\"name\""), "Response should contain name field");
    assertTrue(responseText.contains("\"budget\""), "Response should contain budget field");

    // Verify the values
    assertTrue(responseText.contains("\"name\":\"Test Envelope\""),
        "Response should contain the submitted name");
    assertTrue(responseText.contains("\"budget\":100.5"),
        "Response should contain the submitted budget");
  }

  @Test
  void shouldCreateMultipleEnvelopes() {
    // Test data
    String[][] testEnvelopes = {
        {"Rent", "1200"},
        {"Groceries", "400"},
        {"Utilities", "200"}
    };

    // Navigate to the login page
    page.navigate(baseUrl + "/login");

    // Locate form elements
    var nameInput = page.locator("input[name='name']");
    var budgetInput = page.locator("input[name='budget']");
    var submitButton = page.locator("button:has-text('Save Envelope')");

    // Create each envelope
    for (String[] envelope : testEnvelopes) {
      String name = envelope[0];
      String budget = envelope[1];

      // Fill in the form
      nameInput.fill(name);
      budgetInput.fill(budget);

      // Submit the form and wait for response
      Response response = page.waitForResponse(
          response1 -> response1.url().endsWith("/api/envelopes") &&
              response1.request().method().equals("POST"),
          submitButton::click
      );

      // Verify response status
      assertEquals(201, response.status(),
          String.format("API should return 201 Created status for envelope: %s", name));

      // Parse and verify response
      String responseText = response.text();
      assertTrue(responseText.contains(String.format("\"name\":\"%s\"", name)),
          String.format("Response should contain the submitted name: %s", name));
      assertTrue(responseText.contains(String.format("\"budget\":%s", budget)),
          String.format("Response should contain the submitted budget: %s", budget));

      // Wait a moment before next submission to ensure UI updates
      page.waitForTimeout(500);
    }

    // Get the list of all envelopes from the API
    Response apiResponse = page.waitForResponse(
        response -> response.url().endsWith("/api/envelopes") &&
            response.request().method().equals("GET"),
        () -> page.navigate(baseUrl + "/api/envelopes")
    );

    // Parse the JSON response
    String responseBody = apiResponse.text();

    // Verify each test envelope exists in the response with correct budget
    for (String[] envelope : testEnvelopes) {
      String name = envelope[0];
      String budget = envelope[1];

      // Create a pattern to match the envelope in the JSON response
      String envelopePattern = String.format("\"name\"\s*:\s*\"%s\".*?\"budget\"\s*:\s*%s",
          name, budget);

      // Check if the envelope with matching name and budget exists
      assertTrue(responseBody.matches("(?s).*" + envelopePattern + ".*"),
          String.format("Expected envelope with name '%s' and budget %s not found in response",
              name, budget));
    }

    // Verify each test envelope exists in the response with correct name
    for (String[] envelope : testEnvelopes) {
      String name = envelope[0];
      assertTrue(
          responseBody.contains(String.format("\"name\":\"%s\"", name)),
          String.format("Expected envelope with name '%s' not found in response", name)
      );
    }
  }
}
