package com.jaime.automation.base;

import com.jaime.automation.driver.BrowserType;
import com.jaime.automation.driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

/**
 * BaseTest class is responsible for:
 *  - Managing WebDriver lifecycle per test thread
 *  - Reading system properties (browser)
 *  - Providing a clean initialization with robust retry/waits
 *  - Ensuring thread-safe parallel execution
 */
public abstract class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    // Expose driver to child tests if they need it
    protected WebDriver driver;

    // Critical locator on login page
    private static final By USERNAME_FIELD = By.cssSelector("#user-name, [data-test='username']");

    // App URL
    private static final String APP_URL = "https://www.saucedemo.com/";

    // Retry attempts for page init
    private static final int MAX_RETRY_ATTEMPTS = 3;

    // Generic wait timeout (seconds)
    private static final int WAIT_TIMEOUT = 20;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        log.info("==== Test initialization started ====");

        // 1) Start driver (one per thread) using your DriverManager
        String browserProp = System.getProperty("browser", "chrome");
        BrowserType browser = BrowserType.from(browserProp);
        log.info("Starting WebDriver for browser: {}", browser);
        DriverManager.startDriver(browser);

        // 2) Get thread-local driver
        this.driver = DriverManager.getDriverStatic();

        // 3) Clean state and robustly initialize login page
        driver.manage().deleteAllCookies();
        initializeLoginPageWithRetry();

        log.info("Login page successfully loaded and ready for interaction");
    }

    /** Robust initialization with retry. */
    private void initializeLoginPageWithRetry() {
        Exception lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                log.info("Page load attempt {}/{}", attempt, MAX_RETRY_ATTEMPTS);

                // Navigate + wait DOM ready (+ jQuery idle if present)
                loadPageAndWaitForDOM();

                // Ensure presence/visibility
                waitForLoginPageElements();

                // Ensure interactable
                verifyElementInteractable();

                log.info("Page successfully loaded on attempt {}", attempt);
                return;

            } catch (TimeoutException | NoSuchElementException | StaleElementReferenceException e) {
                lastException = e;
                log.warn("Attempt {}/{} failed: {}", attempt, MAX_RETRY_ATTEMPTS, e.getMessage());

                if (attempt < MAX_RETRY_ATTEMPTS) {
                    log.info("Retrying page load after cleanup...");
                    try {
                        driver.manage().deleteAllCookies();
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        String errorMsg = String.format(
                "Failed to initialize login page after %d attempts. Last error: %s",
                MAX_RETRY_ATTEMPTS, lastException != null ? lastException.getMessage() : "Unknown");
        log.error(errorMsg);
        throw new RuntimeException(errorMsg, lastException);
    }

    /** Navigate and wait for DOM ready (and jQuery idle if present). */
    private void loadPageAndWaitForDOM() {
        driver.get(APP_URL);
        log.debug("Navigated to {}", APP_URL);

        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(WAIT_TIMEOUT))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        // Wait document.readyState == complete
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")));
        log.debug("DOM is complete");

        // Wait jQuery idle if exists
        wait.until(d -> {
            Object ready = ((JavascriptExecutor) d).executeScript(
                    "return (typeof jQuery === 'undefined') ? true : (jQuery.active === 0);");
            return Boolean.TRUE.equals(ready);
        });
        log.debug("jQuery is ready (if present)");
    }

    /** Presence + visibility of login elements. */
    private void waitForLoginPageElements() {
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(WAIT_TIMEOUT))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        wait.until(ExpectedConditions.presenceOfElementLocated(USERNAME_FIELD));
        log.debug("Username field present in DOM");

        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_FIELD));
        log.debug("Username field visible");
    }

    /** Ensure element is clickable and truly rendered. */
    private void verifyElementInteractable() {
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(WAIT_TIMEOUT))
                .pollingEvery(Duration.ofMillis(200))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        WebElement usernameField = wait.until(ExpectedConditions.elementToBeClickable(USERNAME_FIELD));

        if (usernameField.getSize().getHeight() == 0 || usernameField.getSize().getWidth() == 0) {
            throw new IllegalStateException("Username field has zero dimensions - not properly rendered");
        }
        if (!usernameField.isEnabled()) {
            throw new IllegalStateException("Username field is not enabled");
        }

        log.debug("Username field interactable ({}x{}, enabled={})",
                usernameField.getSize().getWidth(),
                usernameField.getSize().getHeight(),
                usernameField.isEnabled());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        log.info("==== Test teardown started ====");
        DriverManager.quitDriver();
        log.info("WebDriver session closed");
        log.info("==== Test finished ====");
    }
}


