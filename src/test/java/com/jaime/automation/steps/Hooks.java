package com.jaime.automation.steps;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import com.jaime.automation.driver.BrowserType;
import com.jaime.automation.driver.DriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cucumber Hooks for WebDriver lifecycle management.
 * @Before runs before each scenario
 * @After runs after each scenario
 */
public class Hooks {

    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    @Before
    public void setUp() {
        log.info("==== Cucumber Scenario Setup ====");

        // Read browser type from system property (default: chrome)
        String browserName = System.getProperty("browser", "chrome");
        BrowserType browser = BrowserType.from(browserName);

        log.info("Starting WebDriver for browser: {}", browser);

        // Start WebDriver for this thread
        DriverManager.startDriver(browser);

        log.info("WebDriver initialized successfully");
    }

    @After
    public void tearDown() {
        log.info("==== Cucumber Scenario Teardown ====");

        // Quit WebDriver for this thread
        DriverManager.quitDriver();

        log.info("WebDriver session closed");
    }
}
