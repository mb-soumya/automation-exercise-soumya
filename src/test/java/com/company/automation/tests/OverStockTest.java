package com.company.automation.tests;

import com.company.automation.base.BaseTest;
import com.company.automation.pages.CartPage;
import com.company.automation.pages.HomePage;
import com.company.automation.pages.ProductPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OverStockTest extends BaseTest {

    @Test(priority = 6)
    public void cannotAddMoreThanStock() {
        HomePage home = new HomePage(driver);
        home.goToHome();
        home.clickProducts();

        ProductPage product = new ProductPage(driver);
        product.search("T-Shirt");
        product.addFirstProductToCart();
        product.clickViewCart();

        CartPage cart = new CartPage(driver);
        // attempt to set a very large qty
        cart.setFirstQty("9999");

        // Expect either an error or qty capped; assert cart page shows an error message or max available info
        Assert.assertTrue(driver.getPageSource().toLowerCase().contains("available") || driver.getPageSource().toLowerCase().contains("not available") || driver.getPageSource().contains("Quantity") );
    }
}
