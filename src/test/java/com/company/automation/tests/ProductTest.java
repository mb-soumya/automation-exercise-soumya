package com.company.automation.tests;

import com.company.automation.base.BaseTest;
import com.company.automation.pages.HomePage;
import com.company.automation.pages.ProductPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductTest extends BaseTest {

    @Test(priority = 2)
    public void searchAndAddToCart() {
        HomePage home = new HomePage(driver);
        home.goToHome();
        home.clickProducts();

        ProductPage product = new ProductPage(driver);
        product.search("T-Shirt");
        product.addFirstProductToCart();
        product.clickViewCart();

        Assert.assertTrue(driver.getPageSource().contains("Shopping Cart") || driver.getTitle().contains("Cart"));
    }
}
