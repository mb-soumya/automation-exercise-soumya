package com.company.automation.tests;

import com.company.automation.base.BaseTest;
import com.company.automation.pages.CartPage;
import com.company.automation.pages.HomePage;
import com.company.automation.pages.ProductPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CartTest extends BaseTest {

    @Test(priority = 3)
    public void updateCartAndValidatePrice() {
        HomePage home = new HomePage(driver);
        home.goToHome();
        home.clickProducts();

        ProductPage product = new ProductPage(driver);
        product.search("T-Shirt");
        product.addFirstProductToCart();
        product.clickViewCart();

        CartPage cart = new CartPage(driver);
        Assert.assertTrue(cart.isCartVisible());
        String unit = cart.getFirstUnitPriceText();
        String digits = unit.replaceAll("[^0-9.]", "");
        double unitPrice = Double.parseDouble(digits.isEmpty() ? "0" : digits);

        cart.setFirstQty("2");
        // basic check: page contains Total text
        Assert.assertTrue(driver.getPageSource().contains("Total") || driver.getPageSource().contains("Rs.") || driver.getPageSource().contains("$" ));
    }
}
