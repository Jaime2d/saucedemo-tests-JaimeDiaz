package com.jaime.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * LoginPage
 *
 * Role:
 *  - Encapsulates the SauceDemo login screen.
 *  - Provides robust, synchronized actions to avoid timing/autofill issues.
 *
 * Notes:
 *  - CSS-first locators.
 *  - Explicit waits before interacting with elements.
 *  - Hard clear (keyboard + JS) to guarantee empty inputs.
 *  - No assertions here; tests assert.
 */
public class LoginPage extends BasePage {

    // AUT base URL
    private static final String URL = "https://www.saucedemo.com/";

    // Locators (stable ids / data-test)
    private static final By USERNAME_INPUT = By.cssSelector("#user-name, [data-test='username']");
    private static final By PASSWORD_INPUT = By.cssSelector("#password, [data-test='password']");
    private static final By LOGIN_BUTTON   = By.cssSelector("#login-button, [data-test='login-button']");
    private static final By ERROR_MESSAGE  = By.cssSelector("[data-test='error'], .error-message-container h3");

    /** Navigates to login page. */
    public LoginPage open() {
        driver.get(URL);
        // Ensure the page is ready and username is visible
        ensureOnLogin();
        return this;
    }

    /** Defensive wait: make sure the login form is present; refresh once if needed. */
    private void ensureOnLogin() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_INPUT));
        } catch (Exception first) {
            driver.navigate().refresh();
            wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_INPUT));
        }
    }

    // ---------- Element getters WITH waits ----------

    private WebElement usernameEl() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_INPUT));
    }

    private WebElement passwordEl() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_INPUT));
    }

    private WebElement loginButtonEl() {
        return wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON));
    }

    // ---------- Actions ----------

    /** Type a username. */
    public LoginPage typeUsername(String username) {
        ensureOnLogin();
        usernameEl().sendKeys(username);
        return this;
    }

    /** Type a password. */
    public LoginPage typePassword(String password) {
        ensureOnLogin();
        passwordEl().sendKeys(password);
        return this;
    }

    /**
     * Ultra-aggressive clear helper with event triggering and verification.
     *
     * Strategy:
     * 1. Focus element and use keyboard shortcuts (CTRL+A, CMD+A, BACKSPACE)
     * 2. JavaScript force clear with event triggering
     * 3. Poll to verify field is actually empty
     * 4. Trigger input/change events for framework detection (React/Angular/Vue)
     *
     * @param locator The locator of the field to clear
     */
    private void hardClear(By locator) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

        // Step 1: Focus and keyboard clear
        el.click();
        el.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        el.sendKeys(Keys.chord(Keys.COMMAND, "a"), Keys.BACK_SPACE);

        // Step 2: JavaScript force clear with autocomplete disabled
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
            "arguments[0].value = '';" +
            "arguments[0].setAttribute('value', '');" +
            "arguments[0].removeAttribute('value');" +
            "arguments[0].autocomplete = 'off';",
            el
        );

        // Step 3: Trigger events (critical for modern frameworks)
        js.executeScript(
            "var element = arguments[0];" +
            "var events = ['input', 'change', 'keyup', 'blur'];" +
            "events.forEach(function(eventType) {" +
            "  var event = new Event(eventType, { bubbles: true, cancelable: true });" +
            "  element.dispatchEvent(event);" +
            "});",
            el
        );

        // Step 4: Selenium clear as final backup
        el.clear();

        // Step 5: Poll until value is confirmed empty (max 5 seconds)
        wait.until(driver -> {
            String value = el.getAttribute("value");
            return value == null || value.isEmpty();
        });
    }

    /** Clear username and assert it is empty. */
    public LoginPage clearUsername() {
        ensureOnLogin();
        hardClear(USERNAME_INPUT);
        String v = usernameEl().getAttribute("value");
        if (v != null && !v.isEmpty()) {
            throw new IllegalStateException("Username input did not clear properly");
        }
        return this;
    }

    /** Clear password and assert it is empty. */
    public LoginPage clearPassword() {
        ensureOnLogin();
        hardClear(PASSWORD_INPUT);
        String v = passwordEl().getAttribute("value");
        if (v != null && !v.isEmpty()) {
            throw new IllegalStateException("Password input did not clear properly");
        }
        return this;
    }

    /** Click Login. */
    public LoginPage clickLogin() {
        loginButtonEl().click();
        return this;
    }

    /**
     * Convenience method for Cucumber/BDD: login with username and password.
     * Performs complete login action: type username → type password → click login.
     *
     * @param username The username to enter
     * @param password The password to enter
     * @return this LoginPage for method chaining
     */
    public LoginPage login(String username, String password) {
        ensureOnLogin();
        typeUsername(username);
        typePassword(password);
        clickLogin();
        return this;
    }

    /** Get error banner text after a failed login. */
    public String getErrorText() {
        WebElement e = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));
        return e.getText().trim();
    }

    /**
     * Alias for getErrorText() - for Cucumber step compatibility.
     * @return Error message text
     */
    public String getErrorMessage() {
        return getErrorText();
    }
}
