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
    void shouldDisplayEnvelopeFormWithRequiredFields() {
        // Navigate to the login page
        page.navigate(baseUrl + "/login");

        // Check if the page title is correct
        String pageTitle = page.title();
        assertTrue(pageTitle.contains("Login") || pageTitle.contains("Budget OK"), 
                "Page title should contain 'Login' or 'Budget OK'");

        // Check for the name input field
        Locator nameInput = page.locator("input[name='name']");
        assertTrue(nameInput.isVisible(), "Name input field should be visible");
        assertEquals("text", nameInput.getAttribute("type"), "Name input should be of type 'text'");
        assertTrue(nameInput.getAttribute("required") != null, "Name input should be required");

        // Check for the budget input field
        Locator budgetInput = page.locator("input[name='budget']");
        assertTrue(budgetInput.isVisible(), "Budget input field should be visible");
        assertEquals("number", budgetInput.getAttribute("type"), "Budget input should be of type 'number'");
        assertEquals("0.01", budgetInput.getAttribute("step"), "Budget step should be 0.01");
        assertEquals("0", budgetInput.getAttribute("min"), "Budget min value should be 0");
        assertTrue(budgetInput.getAttribute("required") != null, "Budget input should be required");

        // Check for the save button
        Locator saveButton = page.locator("button:has-text('Save Envelope')");
        assertTrue(saveButton.isVisible(), "Save button should be visible");
        assertEquals("button", saveButton.getAttribute("type"), "Save button should be of type 'submit'");
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

}
