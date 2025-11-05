package driver;

import driver.strategy.BrowserStrategy;
import driver.strategy.ChromeStrategy;
import driver.strategy.FirefoxStrategy;
import org.openqa.selenium.WebDriver;

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

    public void stop() {
        WebDriver d = threadDriver.get();
        if (d != null) {
            d.quit();
            threadDriver.remove();
        }
    }
}
