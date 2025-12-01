package com.company.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CheckoutPage {
    private WebDriver driver;
    private By proceedToCheckout = By.xpath("//a[contains(text(),'Proceed To Checkout')]");
    private By nameOnCard = By.name("name_on_card");
    private By cardNumber = By.name("card_number");
    private By cvc = By.name("cvc");
    private By expiryMonth = By.name("expiry_month");
    private By expiryYear = By.name("expiry_year");
    private By payButton = By.id("submit");

    public CheckoutPage(WebDriver driver) { this.driver = driver; }

    public void proceedToCheckout() { new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(proceedToCheckout)).click(); }

    public void fillCardDetails(String name, String number, String cvcVal, String m, String y) {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(nameOnCard)).sendKeys(name);
        driver.findElement(cardNumber).sendKeys(number);
        driver.findElement(cvc).sendKeys(cvcVal);
        driver.findElement(expiryMonth).sendKeys(m);
        driver.findElement(expiryYear).sendKeys(y);
        driver.findElement(payButton).click();
    }
}
