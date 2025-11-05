package com.jaime.automation.tests;

import com.jaime.automation.base.BaseTest;
import com.jaime.automation.pages.LoginPage;
import com.jaime.automation.pages.ProductPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
/*
LoginTests
Tests covers in this class:
 *  - UC-1: Empty credentials after clearing → "Username is required"
 *  - UC-2: Password cleared with username present → "Password is required"
 *  - UC-3: Valid user + "secret_sauce" → Product page shows "Swag Labs"
 */
public class LoginTests extends BaseTest {
    /*
     * Data Provider with accepted usernames from SauceDemo login page.
     *
     * IMPORTANT: Removed 'parallel = true' attribute to avoid conflicts with TestNG XML parallelization.
     * Parallelization is now controlled solely by the TestNG suite configuration (testng.xml or testng-parallel.xml).
     * This ensures predictable thread management and avoids race conditions.
     */
    @DataProvider(name = "acceptedUsers")
    public Object[][] acceptedUsers() {
        return new Object[][]{
                {"standard_user"},
                {"problem_user"},
                {"performance_glitch_user"},
                {"error_user"},
                {"visual_user"} //,
               // {"locked_out_user"}//This user is expected to be locked
        };
    }

    /**
     * UC-1 Test Login form with empty credentials:
     * Type any credentials into "Username" and "Password" fields.
     * Clear the inputs. Hit the "Login" button. Check the error messages: "Username is required".
     *
     */
    @Test(description = "UC-1: Empty credentials error after clearing both fields and hit the login button")
    public void uc1_emptyCredentialsShowsUsernameRequired() {
        log.info("UC-1: typing creds, clearing both, then clicking Login");
        LoginPage login = new LoginPage()
                .typeUsername("anything")
                .typePassword("anything")
                .clearUsername()
                .clearPassword()
                .clickLogin();

        String error = login.getErrorText();
        assertThat(error)
                .as("Expected 'Username is required' error banner")
                .containsIgnoringCase("Username is required");
    }

    /**
     UC-2 Test Login form with credentials by passing Username:
     Type any credentials in username. Enter password. Clear the "Password" input.
     Hit the "Login" button. Check the error messages: "Password is required".
    */
    @Test(description = "UC-2: Missing password error after clearing password")
    public void uc2_missingPasswordShowsPasswordRequired() {
        log.info("UC-2: typing creds, clearing password, then clicking Login");
        LoginPage login = new LoginPage()
                .typeUsername("anything")
                .typePassword("anything")
                .clearPassword()
                .clickLogin();
        String error = login.getErrorText();
        assertThat(error)
                .as("Expected 'Password is required' error banner")
                .containsIgnoringCase("Password is required");
    }

    /**
     * UC-3 Test Login form with credentials by passing Username & Password:
     * Type credentials in username which are under Accepted username are sections.
     * Enter password as secret sauce. Click on Login and validate the title “Swag Labs”
     * in the dashboard.
     */
    @Test(dataProvider = "acceptedUsers",
            description = "UC-3: Successful login with accepted username and 'secret_sauce'")
    public void uc3_loginWithUsernameAndPassword(String user) {
        log.info("UC-3: login with accepted users", user);
        new LoginPage()
                .typeUsername(user)
                .typePassword("secret_sauce")
                .clickLogin();

        String header =  new ProductPage().getHeaderText();
        assertThat(header)
                .as("Expected Product page header to be 'Swag Labs'")
                .isEqualTo("Swag Labs");

    }


}
