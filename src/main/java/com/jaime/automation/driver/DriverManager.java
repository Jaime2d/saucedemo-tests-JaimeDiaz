package com.jaime.automation.driver;

import com.jaime.automation.driver.strategy.BrowserStrategy;
import com.jaime.automation.driver.strategy.ChromeStrategy;
import com.jaime.automation.driver.strategy.FirefoxStrategy;
import org.openqa.selenium.WebDriver;
/*
This controller manages, creates, delivers, and closes the WebDriver for each thread.
 */
public class DriverManager {

    // Singleton
    private static final DriverManager INSTANCE = new DriverManager();
    private DriverManager() {}

    public static DriverManager getInstance() {
        return INSTANCE;
    }

    // Un driver por hilo para paralelismo seguro
    private final ThreadLocal<WebDriver> threadDriver = new ThreadLocal<>();

    public void start(BrowserType type) {
        if (threadDriver.get() != null) return; // ya iniciado en este hilo

        BrowserStrategy strategy =
                (type == BrowserType.FIREFOX) ? new FirefoxStrategy() : new ChromeStrategy();

        threadDriver.set(strategy.create());
    }

    public WebDriver getDriver() {
        WebDriver d = threadDriver.get();
        if (d == null) throw new IllegalStateException("WebDriver not started for this thread.");
        return d;
    }

    /**
     * Static convenience method for Cucumber steps and other contexts.
     * Returns the thread-local WebDriver instance.
     *
     * @return WebDriver for current thread
     * @throws IllegalStateException if WebDriver not started for this thread
     */
    public static WebDriver getDriverStatic() {
        return getInstance().getDriver();
    }

    /**
     * Static convenience method for Cucumber hooks.
     * Starts a new WebDriver for the current thread.
     *
     * @param type Browser type to start
     */
    public static void startDriver(BrowserType type) {
        getInstance().start(type);
    }

    /**
     * Static convenience method for Cucumber hooks.
     * Alias for stop() - quits the WebDriver for the current thread.
     */
    public static void quitDriver() {
        getInstance().stop();
    }

    public void stop() {
        WebDriver d = threadDriver.get();
        if (d != null) {
            d.quit();
            threadDriver.remove();
        }
    }
}
