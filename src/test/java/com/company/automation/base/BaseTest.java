package com.company.automation.base;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;
import java.util.Set;

public class BaseTest {

    public WebDriver driver;

    @BeforeMethod
    public void setup() {
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        driver = DriverFactory.createDriver(headless);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) driver.quit();
    }

    // helper to get all cookies as header string
    protected String getCookieHeader() {
        Set<Cookie> cookies = driver.manage().getCookies();
        StringBuilder sb = new StringBuilder();
        for (Cookie c : cookies) {
            if (sb.length() > 0) sb.append("; ");
            sb.append(c.getName()).append("=").append(c.getValue());
        }
        return sb.toString();
    }
}
