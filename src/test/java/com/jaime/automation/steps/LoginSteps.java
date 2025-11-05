package com.jaime.automation.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.assertj.core.api.Assertions.assertThat;

import com.jaime.automation.pages.LoginPage;
import com.jaime.automation.pages.ProductPage;

/**
 * Cucumber Step Definitions for Login functionality.
 * These steps correspond to the scenarios in login.feature.
 *
 * Note: WebDriver is managed by Hooks.java (@Before/@After).
 * PageObjects (LoginPage, ProductPage) obtain the driver automatically from DriverManager via BasePage.
 */
public class LoginSteps {

    private LoginPage loginPage;
    private ProductPage productPage;

    @Given("I am on the Login page")
    public void openLoginPage() {
        // Create LoginPage instance - it will get WebDriver from DriverManager automatically
        loginPage = new LoginPage();
        loginPage.open();
    }

    @When("I try to login without entering a username")
    public void loginWithoutUsername() {
        loginPage.login("", "");
    }

    @When("I enter a username but no password")
    public void loginWithoutPassword() {
        loginPage.login("standard_user", "");
    }

    @When("I login with username {string} and password {string}")
    public void loginWithCredentials(String username, String password) {
        loginPage.login(username, password);
        // Create ProductPage instance - it will get WebDriver from DriverManager automatically
        productPage = new ProductPage();
    }

    @Then("I should see {string}")
    public void verifyError(String expectedError) {
        assertThat(loginPage.getErrorMessage()).containsIgnoringCase(expectedError);
    }

    @Then("I should see the Swag Labs dashboard")
    public void verifyDashboard() {
        assertThat(productPage.getAppLogoText()).contains("Swag Labs");
    }
}

