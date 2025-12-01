package com.company.automation.tests;

import com.company.automation.base.BaseTest;
import com.company.automation.pages.CheckoutPage;
import com.company.automation.pages.HomePage;
import com.company.automation.pages.ProductPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CheckoutTest extends BaseTest {

    @Test(priority = 4)
    public void checkoutFlow() {
        HomePage home = new HomePage(driver);
        home.goToHome();
        home.clickProducts();

        ProductPage product = new ProductPage(driver);
        product.addFirstProductToCart();
        product.clickViewCart();

        CheckoutPage checkout = new CheckoutPage(driver);
        checkout.proceedToCheckout();
        checkout.fillCardDetails("Soumya Test","4242424242424242","123","12","2026");

        Assert.assertTrue(driver.getPageSource().contains("Your order has been placed") || driver.getPageSource().contains("Order Placed") || driver.getPageSource().contains("Invoice"));
    }
}
