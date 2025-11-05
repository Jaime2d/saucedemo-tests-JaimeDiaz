package com.jaime.automation.base;

import com.jaime.automation.driver.BrowserType;
import com.jaime.automation.driver.DriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import java.time.Duration;

/**
 * BaseTest class is responsible for:
 *  - Managing WebDriver lifecycle per test thread
 *  - Reading system properties (browser, headless mode)
 *  - Providing a clean test initialization and teardown flow
 *  - Ensuring robust page load with retry logic
 *
 * This ensures thread-safe parallel execution and centralizes driver setup logic.
 */
public abstract class BaseTest {

    // Using SLF4J so we can plug any logging backend later (Logback, Log4j2, etc.)
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    // Every test will have access to the driver via this method
    protected WebDriver driver;

    // Login page username field locator
    private static final By USERNAME_FIELD = By.cssSelector("#user-name, [data-test='username']");

    // Application URL
    private static final String APP_URL = "https://www.saucedemo.com/";

    // Maximum retry attempts for page load
    private static final int MAX_RETRY_ATTEMPTS = 3;

    // Wait timeout in seconds
    private static final int WAIT_TIMEOUT = 20;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        log.info("==== Test initialization started ====");

        // Read browser type from JVM argument e.g. -Dbrowser=firefox
        String browserName = System.getProperty("browser", "chrome");
        BrowserType browser = BrowserType.from(browserName);

        log.info("Starting WebDriver for browser: {}", browser);

        // Start driver for this thread
        DriverManager.getInstance().start(browser);
        this.driver = DriverManager.getInstance().getDriver();

        // Initialize the login page with retry logic
        initializeLoginPageWithRetry();

        log.info("Login page successfully loaded and ready for interaction");
    }

    /**
     * Initializes the login page with retry logic.
     * Attempts to load the page up to MAX_RETRY_ATTEMPTS times.
     * Each attempt includes navigation, waiting for DOM ready, and element verification.
     */
    private void initializeLoginPageWithRetry() {
        Exception lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                log.info("Page load attempt {}/{}", attempt, MAX_RETRY_ATTEMPTS);

                // Navigate to the application
                loadPageAndWaitForDOM();

                // Wait for the critical element (username field) to be ready
                waitForLoginPageElements();

                // Verify element is actually interactable
                verifyElementInteractable();

                log.info("Page successfully loaded on attempt {}", attempt);
                return; // Success - exit retry loop

            } catch (TimeoutException | NoSuchElementException | StaleElementReferenceException e) {
                lastException = e;
                log.warn("Attempt {}/{} failed: {}", attempt, MAX_RETRY_ATTEMPTS, e.getMessage());

                if (attempt < MAX_RETRY_ATTEMPTS) {
                    log.info("Retrying page load...");
                    // Clean state before retry
                    try {
                        driver.manage().deleteAllCookies();
                        Thread.sleep(500); // Brief pause before retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        // All attempts failed
        String errorMsg = String.format("Failed to initialize login page after %d attempts. Last error: %s",
                MAX_RETRY_ATTEMPTS, lastException != null ? lastException.getMessage() : "Unknown");
        log.error(errorMsg);
        throw new RuntimeException(errorMsg, lastException);
    }

    /**
     * Loads the page and waits for DOM to be completely ready.
     * This ensures the page structure is fully loaded before proceeding.
     */
    private void loadPageAndWaitForDOM() {
        // Navigate to application
        driver.get(APP_URL);
        log.debug("Navigated to {}", APP_URL);

        // Create a FluentWait for better control
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(WAIT_TIMEOUT))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        // Wait for document.readyState to be "complete"
        fluentWait.until(d -> {
            String readyState = ((JavascriptExecutor) d).executeScript("return document.readyState").toString();
            log.debug("Document readyState: {}", readyState);
            return "complete".equals(readyState);
        });

        log.debug("DOM is complete");

        // Additional wait for jQuery if present (SauceDemo uses it)
        fluentWait.until(d -> {
            Object jQueryReady = ((JavascriptExecutor) d).executeScript(
                    "return typeof jQuery !== 'undefined' ? jQuery.active === 0 : true"
            );
            return Boolean.TRUE.equals(jQueryReady);
        });

        log.debug("jQuery is ready (if present)");
    }

    /**
     * Waits for the login page elements to be present and visible in the DOM.
     * Uses FluentWait to handle transient failures gracefully.
     */
    private void waitForLoginPageElements() {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(WAIT_TIMEOUT))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        // Wait for username field to be present in DOM
        fluentWait.until(ExpectedConditions.presenceOfElementLocated(USERNAME_FIELD));
        log.debug("Username field is present in DOM");

        // Wait for username field to be visible
        fluentWait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_FIELD));
        log.debug("Username field is visible");
    }

    /**
     * Verifies that the username field is actually interactable.
     * This final check ensures we can interact with the element before tests begin.
     */
    private void verifyElementInteractable() {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(WAIT_TIMEOUT))
                .pollingEvery(Duration.ofMillis(200))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        // Wait for element to be clickable (implies it's interactable)
        WebElement usernameField = fluentWait.until(ExpectedConditions.elementToBeClickable(USERNAME_FIELD));

        // Verify element dimensions (ensures it's actually rendered)
        if (usernameField.getSize().getHeight() == 0 || usernameField.getSize().getWidth() == 0) {
            throw new IllegalStateException("Username field has zero dimensions - not properly rendered");
        }

        // Verify element is enabled
        if (!usernameField.isEnabled()) {
            throw new IllegalStateException("Username field is not enabled");
        }

        log.debug("Username field is fully interactable (size: {}x{}, enabled: {})",
                usernameField.getSize().getWidth(),
                usernameField.getSize().getHeight(),
                usernameField.isEnabled());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        log.info("==== Test teardown started ====");

        // Quit driver for this specific thread
        DriverManager.getInstance().stop();

        log.info("WebDriver session closed");
        log.info("==== Test finished ====");
    }
}

