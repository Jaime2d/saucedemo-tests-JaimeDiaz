package com.jaime.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
/**
 * ProductPage
 * This class represent the page that we can see when the login is success.
 */
public class ProductPage extends BasePage {

    // The header text shown on inventory page
    private static final By HEADER_TITLE = By.cssSelector(".app_logo");

    /**
     * Gets the header title text: expected "Swag Labs"
     * We expose text retrieval (not assertion) to follow the Page Object role boundaries.
     */
    public String getHeaderText() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(HEADER_TITLE));
        return driver.findElement(HEADER_TITLE).getText().trim();
    }
}
