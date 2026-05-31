package utils;

import base.BaseClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * WaitUtils - Provides explicit wait methods for stable element interactions.
 * Explicit waits are preferred over Thread.sleep() for reliable automation.
 */
public class WaitUtils {

    private static final Logger logger = LogManager.getLogger(WaitUtils.class);

    /**
     * Returns a WebDriverWait object using explicitWait value from config.
     */
    public static WebDriverWait getWait() {
        int explicitWait = Integer.parseInt(BaseClass.config.getProperty("explicitWait", "20"));
        return new WebDriverWait(BaseClass.driver, Duration.ofSeconds(explicitWait));
    }

    /**
     * Waits until element is visible on the page.
     */
    public static void waitForVisibility(WebElement element) {
        try {
            getWait().until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException e) {
            logger.error("Element not visible within timeout: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Waits until element is clickable.
     */
    public static void waitForClickable(WebElement element) {
        try {
            getWait().until(ExpectedConditions.elementToBeClickable(element));
        } catch (TimeoutException e) {
            logger.error("Element not clickable within timeout: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Waits until element is present in DOM using locator.
     */
    public static WebElement waitForPresence(By locator) {
        try {
            return getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.error("Element not found within timeout: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Waits until page title contains the given text.
     */
    public static void waitForTitleContains(String titleText) {
        getWait().until(ExpectedConditions.titleContains(titleText));
    }

    /**
     * Scrolls the page down to bring an element into view.
     */
    public static void scrollToElement(WebElement element) {
        try {
            ((JavascriptExecutor) BaseClass.driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});", element);
            logger.info("Scrolled to element");
        } catch (StaleElementReferenceException e) {
            logger.warn("Stale element during scroll, retrying once");
            ((JavascriptExecutor) BaseClass.driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});", element);
        }
    }

    /**
     * Clicks an element safely using JavaScript if normal click is intercepted.
     */
    public static void safeClick(WebElement element) {
        try {
            waitForClickable(element);
            element.click();
        } catch (ElementClickInterceptedException e) {
            logger.warn("Normal click intercepted, using JavaScript click");
            ((JavascriptExecutor) BaseClass.driver).executeScript("arguments[0].click();", element);
        }
    }
}
