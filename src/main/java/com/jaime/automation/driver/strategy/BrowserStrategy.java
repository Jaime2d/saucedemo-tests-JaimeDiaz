package com.jaime.automation.driver.strategy;

import org.openqa.selenium.WebDriver;
/* We use this interface to secure the create method that returns a WebDriver
*/
public interface BrowserStrategy {
    WebDriver create();
}

