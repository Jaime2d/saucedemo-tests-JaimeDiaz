package com.jaime.automation.pages;

import com.jaime.automation.driver.DriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/**
 * BasePage
 *
 * Purpose:
 *  - Provide shared WebDriver and WebDriverWait to all page objects.
 *  - Centralize common configuration (timeouts, utilities).
 *
 * Reason:
 *  - Keeps page classes small and focused on business actions.
 *  - Reduces duplication and improves maintainability.
 *
 * Wait Strategy:
 *  - Uses 20-second timeout to match BaseTest configuration
 *  - Explicit waits only (no implicit waits)
 */
public abstract class BasePage {

    // Protected so subclasses can use them directly
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    // Consistent timeout across framework (matches BaseTest)
    private static final int DEFAULT_WAIT_TIMEOUT = 20;

    protected BasePage() {
        // Get the thread-local driver managed by DriverManager
        this.driver = DriverManager.getInstance().getDriver();

        // Explicit wait used by all pages - consistent with BaseTest
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT));
    }

    /**
     * Simple helper to fetch current page title.
     * Useful for lightweight assertions when needed.
     */
    public String getTitle() {
        return driver.getTitle();
    }
}
