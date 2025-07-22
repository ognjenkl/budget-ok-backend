package com.ognjen.budgetok.acceptance;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
    // Given
    String[] envelope = {"Test Envelope", "100.5"};

    // When
    navigator.navigateTo(baseUrl);

    navigator.submitRequestToCreateEnvelope(envelope, "/api/envelopes", "POST");

    // Then
    assertTrue(
        navigator.isEnvelopeVisible(envelope[0], envelope[1]),
        String.format("Envelope with name '%s' and budget '%s' should be visible on the page",
            envelope[0], envelope[1])
    );
  }

  @Test
  void shouldCreateMultipleEnvelopes() {
    // Given
    String[][] testEnvelopes = createThreeEnvelopes();

    // When
    navigator.navigateTo(baseUrl);

    // Create all envelopes
    for (String[] envelope : testEnvelopes) {
      navigator.submitRequestToCreateEnvelope(envelope, "/api/envelopes", "POST");
      navigator.waitForTimeout(500);
    }

    // Then verify all envelopes are visible on the page
    for (String[] envelope : testEnvelopes) {
      String name = envelope[0];
      String budget = envelope[1];

      assertTrue(
          navigator.isEnvelopeVisible(name, budget),
          String.format("Envelope with name '%s' and budget '%s' should be visible on the page",
              name, budget)
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
}
