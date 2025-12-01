package com.company.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public class CartPage {
    private WebDriver driver;
    private By cartTable = By.cssSelector(".cart_info");
    private By firstUnitPrice = By.cssSelector(".cart_price");
    private By firstQtyInput = By.cssSelector(".cart_quantity input");
    private By updateBtn = By.cssSelector("button.update");
    private By removeLink = By.cssSelector("a.cart_quantity_delete");

    public CartPage(WebDriver driver) { this.driver = driver; }

    public boolean isCartVisible() {
        try {
            // common cart container(s)
            if (!driver.findElements(By.cssSelector(".cart_info, .table-condensed, .shopping_cart, #cart_info")).isEmpty()) {
                return driver.findElement(By.cssSelector(".cart_info, .table-condensed, .shopping_cart, #cart_info")).isDisplayed();
            }
            // fallback: check for text marker
            return driver.getPageSource().contains("Shopping Cart") || driver.getPageSource().contains("Your shopping cart") ||
                    driver.getPageSource().contains("Shopping Cart");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get the first unit price text found in the cart.
     * Tries several likely selectors.
     */
    public String getFirstUnitPriceText() {
        // candidate selectors for unit price cells
        By[] candidates = new By[] {
                By.cssSelector(".cart_price"),
                By.cssSelector("td.cart_price"),
                By.cssSelector(".product_price"),
                By.xpath("//tr[1]//td[contains(@class,'price') or contains(text(),'Rs') or contains(text(),'$')]"),
                By.xpath("(//td[contains(.,'Rs.') or contains(.,'$') or contains(@class,'price')])[1]")
        };

        for (By c : candidates) {
            try {
                List<WebElement> found = driver.findElements(c);
                if (!found.isEmpty()) {
                    for (WebElement el : found) {
                        if (el.isDisplayed() && !el.getText().trim().isEmpty()) {
                            return el.getText().trim();
                        }
                    }
                }
            } catch (StaleElementReferenceException ignored) { }
        }

        // Fallback: try first cell that looks like a price near the quantity input
        try {
            WebElement qty = driver.findElement(By.xpath("(//input[@name='quantity' or contains(@class,'cart_quantity') or @type='number'])[1]"));
            WebElement parentRow = qty.findElement(By.xpath("./ancestor::tr[1]"));
            List<WebElement> tds = parentRow.findElements(By.tagName("td"));
            for (WebElement td : tds) {
                String t = td.getText();
                if (t != null && (t.contains("Rs") || t.contains("$") || t.matches(".*\\d+.*"))) return t.trim();
            }
        } catch (Exception ignored) {}

        return ""; // not found
    }

    /**
     * Set the first quantity in cart to `qty`. Uses JS to set value and triggers events,
     * then clicks update button if present. Waits until the cart total or line total updates.
     */
    public void setFirstQty(String qty) {
        WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // 1) locate qty input with multiple fallbacks
        By[] qtyCandidates = new By[] {
                By.xpath("(//input[@name='quantity' or contains(@class,'cart_quantity_input') or contains(@class,'cart_quantity') or @type='number'])[1]"),
                By.cssSelector("input[name='quantity']"),
                By.cssSelector("input.cart_quantity_input"),
                By.xpath("(//input[@type='number'])[1]"),
                By.xpath("(//input[contains(@id,'quantity')])[1]")
        };

        WebElement qtyInput = null;
        for (By qc : qtyCandidates) {
            try {
                List<WebElement> list = driver.findElements(qc);
                if (!list.isEmpty()) {
                    for (WebElement w : list) {
                        try {
                            if (w.isDisplayed() && w.getSize().getHeight() > 0 && w.getSize().getWidth() > 0) {
                                qtyInput = w;
                                break;
                            }
                        } catch (StaleElementReferenceException ignored) {}
                    }
                }
            } catch (Exception ignored) {}
            if (qtyInput != null) break;
        }

        if (qtyInput == null) {
            throw new NoSuchElementException("Quantity input not found on cart page using known selectors.");
        }

        // 2) capture current visible total (line or cart total) to wait for change after update
        String beforeTotal = "";
        By[] totalCandidates = new By[] {
                By.cssSelector(".cart_total"),
                By.cssSelector(".cart-subtotal, .total_price, .cart_total_price"),
                By.xpath("(//td[contains(.,'Total') or contains(@class,'total')])[1]"),
                By.xpath("(//td[contains(text(),'Rs') or contains(text(),'$')])[last()]")
        };
        for (By tc : totalCandidates) {
            try {
                List<WebElement> found = driver.findElements(tc);
                if (!found.isEmpty()) {
                    for (WebElement e : found) {
                        if (e.isDisplayed() && !e.getText().trim().isEmpty()) {
                            beforeTotal = e.getText().trim(); break;
                        }
                    }
                }
            } catch (Exception ignored) {}
            if (!beforeTotal.isEmpty()) break;
        }

        // 3) set value using JS (more reliable than clear/sendKeys in some carts)
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change'));", qtyInput, qty);
        } catch (Exception e) {
            // fallback: try normal clear/sendKeys
            try {
                qtyInput.clear();
                qtyInput.sendKeys(qty);
            } catch (Exception ex) {
                throw new RuntimeException("Unable to set quantity value", ex);
            }
        }

        // 4) try clicking update button if exists (some sites require explicit update)
        By[] updateCandidates = new By[] {
                By.cssSelector("button.update, button.btn.update, input.update"),
                By.xpath("//button[contains(.,'Update') or contains(.,'update') or contains(.,'Update Cart')]"),
                By.xpath("//a[contains(.,'Update') or contains(.,'update')]")
        };
        boolean clickedUpdate = false;
        for (By uc : updateCandidates) {
            try {
                List<WebElement> up = driver.findElements(uc);
                if (!up.isEmpty()) {
                    for (WebElement u : up) {
                        try {
                            if (u.isDisplayed() && u.isEnabled()) {
                                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", u);
                                u.click();
                                clickedUpdate = true;
                                break;
                            }
                        } catch (Exception ignored) {}
                    }
                }
            } catch (Exception ignored) {}
            if (clickedUpdate) break;
        }

        // 5) Wait for total or line total to reflect change (or at least detect DOM update)
        try {
            final String before = beforeTotal;
            localWait.until((drv) -> {
                try {
                    // prefer cart_total cell
                    for (By tc : totalCandidates) {
                        List<WebElement> found = drv.findElements(tc);
                        if (!found.isEmpty()) {
                            for (WebElement e : found) {
                                if (e.isDisplayed()) {
                                    String now = e.getText().trim();
                                    if (!now.equals(before)) return true;
                                }
                            }
                        }
                    }
                    // fallback: if an AJAX call updated some cart counter, detect page source change
                    return !drv.getPageSource().contains(before);
                } catch (StaleElementReferenceException sere) {
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            });
        } catch (Exception waitEx) {
            // if wait timed out, still continue; we attempted to update
        }
    }}
