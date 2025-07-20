package com.ognjen.budgetok.acceptance;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Response;

public class Navigator {

  private Playwright playwright;
  private Browser browser;
  private BrowserContext context;
  private Page page;

  public Navigator() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
        .setHeadless(false) // Set to true in CI environment
        .setSlowMo(50));
  }

  public void initContext() {
    context = browser.newContext();
    page = context.newPage();
  }

  public void close() {
    if (playwright != null) {
      playwright.close();
    }
  }

  public void closeContext() {
    if (context != null) {
      context.close();
    }
  }

  public void navigateTo(String url) {
    page.navigate(url);
  }

  public Response sendRequestToCreateEnvelope(String[] envelope, String path, String method) {
    // Locate form element
    Locator nameInput = page.locator("input[name='name']");
    // Locate form element
    Locator budgetInput = page.locator("input[name='budget']");
    // Locate form element
    Locator submitButton = page.locator("button:has-text('Save Envelope')");

    // Fill in the form
    nameInput.fill(envelope[0]);
    budgetInput.fill(envelope[1]);

    // Submit the form and wait for response
    return getResponse(path, method, submitButton::click);

  }

  private Response getResponse(String path, String method, Runnable callback) {

    return page.waitForResponse(
        response -> isPreconditionStisfied(path, method, response),
        callback
    );
  }

  private boolean isPreconditionStisfied(String path, String method, Response response) {
    return response.url().endsWith(path) && response.request().method().equals(method);
  }

  public void waitForTimeout(int timeout) {
    // Wait a moment before next submission to ensure UI updates
    page.waitForTimeout(timeout);
  }

  public Response sendRequestToGetEnvelopes(String baseUrl, String path, String method) {
      return getResponse(path, method, () -> navigateTo(baseUrl + path));
  }
}
