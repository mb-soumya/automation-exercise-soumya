package com.company.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private By loginEmail = By.cssSelector("input[data-qa='login-email']");
    private By loginPassword = By.cssSelector("input[data-qa='login-password']");
    private By loginButton = By.cssSelector("button[data-qa='login-button']");

    public LoginPage(WebDriver driver) { this.driver = driver; }

    public void login(String email, String password) {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(loginEmail)).sendKeys(email);
        driver.findElement(loginPassword).sendKeys(password);
        driver.findElement(loginButton).click();
    }
}
