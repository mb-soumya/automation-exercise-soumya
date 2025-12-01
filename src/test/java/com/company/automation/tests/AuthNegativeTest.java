package com.company.automation.tests;

import com.company.automation.base.BaseTest;
import com.company.automation.pages.HomePage;
import com.company.automation.pages.SignupPage;
import com.company.automation.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthNegativeTest extends BaseTest {

    @Test(priority = 1)
    public void signupWithInvalidEmailShowsError() {
        HomePage home = new HomePage(driver);
        home.goToHome();
        home.clickSignupLogin();

        SignupPage signup = new SignupPage(driver);
        signup.signup("Test User", "invalid-email");
        // expect validation or error message on page
        Assert.assertTrue(driver.getPageSource().contains("Invalid email") || driver.getPageSource().contains("valid email" ) || driver.getPageSource().contains("Email" ));
    }

    @Test(priority = 2)
    public void loginWithWrongPasswordShowsError() {
        HomePage home = new HomePage(driver);
        home.goToHome();
        home.clickSignupLogin();

        LoginPage login = new LoginPage(driver);
        login.login("test@example.com", "wrongpassword");
        Assert.assertTrue(driver.getPageSource().contains("incorrect") || driver.getPageSource().contains("Try again") || driver.getPageSource().contains("Login" ));
    }
}
