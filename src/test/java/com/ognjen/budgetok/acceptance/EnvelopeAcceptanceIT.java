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
        assertEquals("submit", saveButton.getAttribute("type"), "Save button should be of type 'submit'");
    }

    @Test
    void shouldAllowSubmittingEnvelopeForm() {
        // Navigate to the login page
        page.navigate(baseUrl + "/login");

        // Fill in the form
        page.fill("input[name='name']", "Test Envelope");
        page.fill("input[name='budget']", "100.50");

        // Submit the form
        page.click("button:has-text('Save Envelope')");

        // Add assertions for successful form submission
        // This will depend on how your application handles the form submission
        // For example, you might check for a success message or redirect
    }
}
