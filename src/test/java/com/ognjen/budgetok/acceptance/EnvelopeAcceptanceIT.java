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

import static org.junit.jupiter.api.Assertions.assertFalse;
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

    navigator.createEnvelope(envelope);

    // Then
    String successMessage = "Envelope created successfully!";
    assertTrue(
        navigator.isTextVisible(successMessage),
        String.format("Success message '%s' should be visible on the page", successMessage)
    );
  }

  @Test
  void shouldCreateMultipleEnvelopes() {
    // Given
    String[][] testEnvelopes = createThreeEnvelopes();

    // When and Then
    navigator.navigateTo(baseUrl);

    for (String[] envelope : testEnvelopes) {

      navigator.createEnvelope(envelope);

      navigator.waitForTimeout(500);
      String successMessage = "Envelope created successfully!";
      assertTrue(
          navigator.isTextVisible(successMessage),
          String.format("Success message '%s' should be visible on the page", successMessage)
      );

      navigator.waitForTimeout(3000);
      assertFalse(
          navigator.isTextVisible(successMessage),
          String.format("Success message '%s' should not be visible on the page", successMessage)
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
