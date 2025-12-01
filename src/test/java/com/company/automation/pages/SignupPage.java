package com.company.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class SignupPage {
    private WebDriver driver;
    private By nameInput = By.cssSelector("input[data-qa='signup-name']");
    private By emailInput = By.cssSelector("input[data-qa='signup-email']");
    private By signupButton = By.cssSelector("button[data-qa='signup-button']");

    public SignupPage(WebDriver driver) { this.driver = driver; }

    public void signup(String name, String email) {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(nameInput)).sendKeys(name);
        driver.findElement(emailInput).sendKeys(email);
        driver.findElement(signupButton).click();
    }
}
