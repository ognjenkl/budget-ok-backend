package com.ognjen.budgetok.acceptance;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;

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

  private Response submitRequestToCreateEnvelope(String[] envelope, String path, String method) {

    Locator nameInput = page.getByPlaceholder("Name");
    Locator budgetInput = page.getByPlaceholder("Budget");
    Locator submitButton = page.getByRole(AriaRole.BUTTON,
        new Page.GetByRoleOptions().setName("Submit"));

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

  public boolean isEnvelopeVisible(String name, String budget) {
    try {
      // Find the row containing the envelope name and budget
      String xpath = String.format(
          "//tr[.//*[contains(text(),'%s')] and .//*[contains(text(),'%s')]]", name, budget);
      page.waitForSelector("xpath=" + xpath, new Page.WaitForSelectorOptions().setTimeout(2000));
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public void createEnvelope(String[] envelope) {
    submitRequestToCreateEnvelope(envelope, "/api/envelopes", "POST");
  }
}
