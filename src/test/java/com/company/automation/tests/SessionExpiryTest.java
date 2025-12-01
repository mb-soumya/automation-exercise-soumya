package com.company.automation.tests;

import com.company.automation.base.BaseTest;
import com.company.automation.pages.HomePage;
import org.openqa.selenium.Cookie;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SessionExpiryTest extends BaseTest {

    @Test(priority = 7)
    public void sessionInvalidatedShowsLogin() {
        HomePage home = new HomePage(driver);
        home.goToHome();

        // Emulate login cookie set (this is placeholder; real login would set cookies)
        driver.manage().addCookie(new Cookie("fake_session", "12345"));
        // delete cookies to emulate expiry
        driver.manage().deleteAllCookies();
        driver.navigate().refresh();

        // After cookie deletion, site should show login/signup option
        Assert.assertTrue(driver.getPageSource().contains("Signup / Login") || driver.getPageSource().contains("Login") );
    }
}
