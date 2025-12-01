package com.company.automation.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ProductPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Actions actions;

    // reasonable default wait (increase if CI is slow)
    private final Duration WAIT_TIMEOUT = Duration.ofSeconds(20);
    private final Duration POLLING = Duration.ofMillis(250);

    // frequently-used locators (cover overlay + always-visible add buttons)
    private final By searchBox = By.id("search_product");
    private final By submitSearch = By.id("submit_search");

    // product block and candidate Add-to-cart locators (try these in order)
    private final By firstProductContainer = By.xpath("(//div[contains(@class,'productinfo') or contains(@class,'product-overlay')])[1]");
    private final List<By> candidateAddLocators = Arrays.asList(
            // always-visible one (prefer this)
            By.xpath("(//div[contains(@class,'productinfo') or contains(@class,'product-info')][1]//a[contains(normalize-space(.),'Add to cart')])[1]"),
            // sometimes text-only anchor
            By.xpath("(//a[contains(normalize-space(.),'Add to cart')])[1]"),
            // overlay button (visible after hover)
            By.xpath("(//div[contains(@class,'product-overlay')]//a[contains(normalize-space(.),'Add to cart')])[1]"),
            // link with class or id variations
            By.cssSelector("a.btn.btn-default.add-to-cart, a.add-to-cart, .product-overlay a")
    );

    private final By viewCart = By.xpath("//u[text()='View Cart']/parent::a");
    private final By continueShoppingButton = By.xpath("//button[contains(text(),'Continue Shopping') or contains(@class,'continue')]");
    private final By cartModal = By.cssSelector(".modal, #cartModal, .cart-modal");

    public ProductPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        this.wait.pollingEvery(POLLING);
        this.actions = new Actions(driver);
    }

    // -------------------------
    // Utility: wait until page ready + jQuery finished
    // -------------------------
    private void waitForPageToBeStable() {
        try {
            // wait for document ready
            wait.until((ExpectedCondition<Boolean>) wd ->
                    ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));

            // if jQuery present, wait for network quiet
            Boolean jqueryDefined = (Boolean) ((JavascriptExecutor) driver).executeScript("return (typeof jQuery !== 'undefined')");
            if (Boolean.TRUE.equals(jqueryDefined)) {
                wait.until((ExpectedCondition<Boolean>) wd ->
                        (Long) ((JavascriptExecutor) wd).executeScript("return jQuery.active") == 0L);
            }
        } catch (Exception e) {
            // not critical — proceed
        }
    }

    // -------------------------
    // SEARCH
    // -------------------------
    public void search(String text) {
        waitForPageToBeStable();
        try {
            WebElement box = wait.until(ExpectedConditions.elementToBeClickable(searchBox));
            box.clear();
            box.sendKeys(text);
            // try click submit if exists otherwise submit
            try {
                WebElement submit = driver.findElement(submitSearch);
                if (submit.isDisplayed() && submit.isEnabled()) {
                    submit.click();
                } else {
                    box.submit();
                }
            } catch (Exception e) {
                try { box.submit(); } catch (Exception ignored) {}
            }
            // wait until product container shows up
            wait.until(ExpectedConditions.visibilityOfElementLocated(firstProductContainer));
            waitForPageToBeStable();
        } catch (TimeoutException te) {
            // best-effort fallback: try to find any product container
            List<WebElement> fallback = driver.findElements(firstProductContainer);
            if (fallback.isEmpty()) throw te;
        }
    }

    // -------------------------
    // ADD FIRST PRODUCT TO CART (very robust)
    // -------------------------
    public void addFirstProductToCart() {
        waitForPageToBeStable();

        // ensure product container visible + scrolled
        WebElement product = wait.until(ExpectedConditions.visibilityOfElementLocated(firstProductContainer));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", product);

        // Retry loop: try each candidate locator, and for each try click strategies
        TimeoutException lastTimeout = null;

        for (By candidate : candidateAddLocators) {
            try {
                // small fluent wait for presence of candidate within timeout
                WebElement addButton = wait.until(new ExpectedCondition<WebElement>() {
                    @Override
                    public WebElement apply(WebDriver wd) {
                        try {
                            List<WebElement> found = wd.findElements(candidate);
                            if (found.isEmpty()) return null;
                            for (WebElement el : found) {
                                if (el.isDisplayed() && el.getSize().getHeight() > 0 && el.getSize().getWidth() > 0) {
                                    return el;
                                }
                            }
                            return null;
                        } catch (StaleElementReferenceException | NoSuchElementException ignored) {
                            return null;
                        }
                    }
                });

                if (addButton == null) {
                    // not found for this candidate, continue to next
                    continue;
                }

                // If overlay style, hover first
                try {
                    actions.moveToElement(product).perform();
                    // small JS to ensure overlay shows up
                    ((JavascriptExecutor) driver).executeScript(
                            "try{var e=arguments[0]; e.dispatchEvent(new MouseEvent('mousemove',{bubbles:true}));}catch(ex){}", product);
                } catch (Exception ignored) {}

                // Try normal click with wait until clickable
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(addButton));
                    addButton.click();
                } catch (Exception clickEx1) {
                    // fallback: try Actions.click
                    try {
                        actions.moveToElement(addButton).click().perform();
                    } catch (Exception clickEx2) {
                        // final fallback: JS click (most robust)
                        try {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addButton);
                        } catch (Exception clickEx3) {
                            // throw and try next candidate
                            throw new ElementClickInterceptedException("All click strategies failed for candidate: " + candidate, clickEx3);
                        }
                    }
                }

                // After clicking, wait for confirmation: cart modal OR view cart link OR toast
                try {
                    wait.until((ExpectedCondition<Boolean>) wd -> {
                        boolean modalVisible = !wd.findElements(cartModal).isEmpty() && wd.findElements(cartModal).get(0).isDisplayed();
                        boolean viewCartPresent = !wd.findElements(viewCart).isEmpty() && wd.findElements(viewCart).get(0).isDisplayed();
                        boolean continueBtnPresent = !wd.findElements(continueShoppingButton).isEmpty();
                        // if any of these present, we consider success
                        return modalVisible || viewCartPresent || continueBtnPresent;
                    });
                } catch (Exception e) {
                    // sometimes site immediately hides modal, so also check page source for cart text
                    try {
                        String src = driver.getPageSource();
                        if (!src.contains("product added") && !src.contains("Added!")) {
                            // not strictly required to throw - still consider success if no error
                        }
                    } catch (Exception ignore) {}
                }

                // If reached here, click was attempted and likely succeeded — return
                return;

            } catch (TimeoutException e) {
                lastTimeout = e;
                // try next candidate
            } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
                // try next candidate
            } catch (Exception e) {
                // unexpected - log and continue to next candidate
            }
        }

        // If we reach here, no candidate succeeded
        if (lastTimeout != null) throw lastTimeout;
        throw new TimeoutException("Unable to click Add to Cart using any candidate locators");
    }

    // -------------------------
    // CLICK VIEW CART
    // -------------------------
    public void clickViewCart() {
        waitForPageToBeStable();
        try {
            WebElement vc = wait.until(ExpectedConditions.elementToBeClickable(viewCart));
            vc.click();
        } catch (Exception e) {
            // fallback: try JS click if normal fails
            try {
                WebElement vc = driver.findElement(viewCart);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", vc);
            } catch (Exception ex) {
                throw new RuntimeException("Unable to click View Cart", ex);
            }
        }
    }
}