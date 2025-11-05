package com.jaime.automation.driver.strategy;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
/*
We use this class to instantiate the Firefox webdriver with options
 */
public class FirefoxStrategy implements BrowserStrategy {
    @Override
    public WebDriver create() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();

        // Desactivar autofill / password manager en Firefox
        options.addPreference("signon.rememberSignons", false);
        options.addPreference("signon.autofillForms", false);
        options.addPreference("signon.formlessCapture.enabled", false);
        options.addPreference("extensions.formautofill.creditCards.enabled", false);
        options.addPreference("extensions.formautofill.addresses.enabled", false);

        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("-headless");
        }
        return new FirefoxDriver(options);
    }
}
