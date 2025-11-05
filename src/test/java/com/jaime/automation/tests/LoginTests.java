package com.jaime.automation.tests;
import com.jaime.automation.base.BaseTest;
import com.jaime.automation.pages.LoginPage;
import com.jaime.automation.pages.ProductPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LoginTests
 * Cubre:
 *  - UC-1: Credenciales vacías → "Username is required"
 *  - UC-2: Falta password → "Password is required"
 *  - UC-3: Usuario válido + "secret_sauce" → "Swag Labs"
 */
public class LoginTests extends BaseTest {

    @DataProvider(name = "acceptedUsers")
    public Object[][] acceptedUsers() {
        return new Object[][]{
                {"standard_user"},
                {"problem_user"},
                {"performance_glitch_user"},
                {"error_user"},
                {"visual_user"}
                // {"locked_out_user"} // este usuario falla a propósito; déjalo fuera
        };
    }

    @Test(description = "UC-1: Empty credentials error after clearing both fields and hit the login button")
    public void uc1_emptyCredentialsShowsUsernameRequired() {
        log.info("UC-1: Forcing initial state and testing empty credentials");
        LoginPage login = new LoginPage();
        login.open(); // estado inicial garantizado

        login.typeUsername("anything")
                .typePassword("anything")
                .clearUsername()
                .clearPassword()
                .clickLogin();

        String error = login.getErrorText();
        assertThat(error)
                .as("Expected 'Username is required' error banner")
                .containsIgnoringCase("Username is required");
    }

    @Test(description = "UC-2: Missing password error after clearing password")
    public void uc2_missingPasswordShowsPasswordRequired() {
        log.info("UC-2: Forcing initial state and testing missing password");
        LoginPage login = new LoginPage();
        login.open(); // estado inicial garantizado

        login.typeUsername("anything")
                .typePassword("anything")
                .clearPassword()
                .clickLogin();

        String error = login.getErrorText();
        assertThat(error)
                .as("Expected 'Password is required' error banner")
                .containsIgnoringCase("Password is required");
    }

    @Test(dataProvider = "acceptedUsers",
            description = "UC-3: Successful login with accepted username and 'secret_sauce'")
    public void uc3_loginWithUsernameAndPassword(String user) {
        log.info("UC-3: login with accepted user {}", user); // usa placeholder

        LoginPage login = new LoginPage();
        login.open(); // estado inicial garantizado

        login.typeUsername(user)
                .typePassword("secret_sauce")
                .clickLogin();

        ProductPage products = new ProductPage();
        // Usa el método real de tu Page Object:
        String header = products.getHeaderText(); // o getAppLogoText()
        assertThat(header)
                .as("Expected Product page header to be 'Swag Labs'")
                .contains("Swag Labs"); // equals si tu método devuelve exactamente "Swag Labs"
    }
}
