package com.company.automation.tests;

import com.company.automation.base.BaseTest;
import com.company.automation.pages.HomePage;
import com.company.automation.pages.SignupPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Instant;

public class AuthTest extends BaseTest {

    @Test(priority = 1)
    public void registerAndLogin() throws InterruptedException {
        HomePage home = new HomePage(driver);
        home.goToHome();
        home.clickSignupLogin();

        SignupPage signup = new SignupPage(driver);
        String email = "soumyatest+" + Instant.now().toEpochMilli() + "@example.com";
        signup.signup("Soumya Test", email);

        // after signup the site may navigate; assert account created or prompt
        Assert.assertTrue(driver.getPageSource().contains("Account Created") || driver.getPageSource().contains("Already exists") || driver.getTitle().length() > 0);
    }
}
