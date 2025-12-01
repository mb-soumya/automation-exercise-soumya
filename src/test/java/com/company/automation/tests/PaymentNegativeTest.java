package com.company.automation.tests;

import com.company.automation.base.BaseTest;
import com.company.automation.pages.CheckoutPage;
import com.company.automation.pages.HomePage;
import com.company.automation.pages.ProductPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PaymentNegativeTest extends BaseTest {

    @Test(priority = 5)
    public void invalidCardShowsError() {
        HomePage home = new HomePage(driver);
        home.goToHome();
        home.clickProducts();

        ProductPage product = new ProductPage(driver);
        product.addFirstProductToCart();
        product.clickViewCart();

        CheckoutPage checkout = new CheckoutPage(driver);
        checkout.proceedToCheckout();
        // invalid short card
        checkout.fillCardDetails("Soumya Test","1234","12","1","2020");

        Assert.assertTrue(driver.getPageSource().contains("Incorrect") || driver.getPageSource().contains("invalid") || driver.getPageSource().contains("declined"));
    }
}
