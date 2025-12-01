package com.company.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class HomePage {
    private WebDriver driver;
    private By signupLogin = By.xpath("//a[contains(text(),'Signup / Login')]");
    private By productsLink = By.xpath("//a[contains(text(),'Products')]");
    private String baseUrl;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.baseUrl = System.getProperty("base.url", "https://automationexercise.com/");
    }

    public void goToHome() {
        driver.get(baseUrl);
    }

    public void clickSignupLogin() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(signupLogin)).click();
    }

    public void clickProducts() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(productsLink)).click();
    }
}
