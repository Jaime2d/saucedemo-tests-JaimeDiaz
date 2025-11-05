package com.jaime.automation.driver.strategy;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.HashMap;
import java.util.Map;

/**
 * ChromeStrategy - Creates Chrome WebDriver with optimized settings
 *
 * Optimizations for:
 *  - macOS ARM (Apple Silicon) compatibility
 *  - Chrome 142+ stability
 *  - Consistent page load behavior
 *  - Parallel test execution
 */
public class ChromeStrategy implements BrowserStrategy {
    @Override
    public WebDriver create() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        // ========== Page Load Strategy ==========
        // NORMAL: Wait for document readyState = "complete" (recommended for stability)
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        // ========== Preferences (Aggressive autofill/password manager disabling) ==========
        Map<String, Object> prefs = new HashMap<>();
        // Disable password manager
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);

        // Disable all autofill features
        prefs.put("autofill.profile_enabled", false);
        prefs.put("autofill.enabled", false);
        prefs.put("autofill.credit_card_enabled", false);

        // Disable password generation
        prefs.put("password_manager.password_generation_enabled", false);

        // Disable save password prompts
        prefs.put("profile.default_content_setting_values.notifications", 2);

        options.setExperimentalOption("prefs", prefs);

        // ========== Chrome Arguments ==========
        // Disable password save prompts
        options.addArguments("--disable-save-password-bubble");

        // Disable browser extensions for consistent behavior
        options.addArguments("--disable-extensions");

        // Disable popup blocking
        options.addArguments("--disable-popup-blocking");

        // Disable browser notifications
        options.addArguments("--disable-notifications");

        // Disable GPU for headless stability (helps with macOS ARM)
        options.addArguments("--disable-gpu");

        // Disable dev shm usage (prevents crashes in containerized environments)
        options.addArguments("--disable-dev-shm-usage");

        // Disable browser automation detection flags
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        // No sandbox (only if needed for CI/CD or Docker)
        // Uncomment next line if you encounter sandbox issues
        // options.addArguments("--no-sandbox");

        // ========== Window Management ==========
        // Headless mode (optional via -Dheadless=true)
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
            // Set window size for headless (important for element visibility)
            options.addArguments("--window-size=1920,1080");
        } else {
            // Maximize window for non-headless
            options.addArguments("--start-maximized");
        }

        // ========== Logging (optional - helps debug driver issues) ==========
        // Reduce Chrome logs
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-logging"});

        return new ChromeDriver(options);
    }
}
