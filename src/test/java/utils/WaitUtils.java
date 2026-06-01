package utils;

import base.BaseClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtils {

    private static final Logger logger = LogManager.getLogger(WaitUtils.class);

    public static WebDriverWait getWait() {
        return new WebDriverWait(BaseClass.driver, Duration.ofSeconds(10));
    }

    public static void waitForVisibility(WebElement element) {
        try {
            getWait().until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            logger.warn("Element not visible");
        }
    }

    public static void waitForClickable(WebElement element) {
        try {
            getWait().until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            logger.warn("Element not clickable");
        }
    }

    public static void waitForTitleContains(String titleText) {
        try {
            getWait().until(ExpectedConditions.titleContains(titleText));
        } catch (Exception e) {
            logger.warn("Title not updated");
        }
    }

    public static void safeClick(WebElement element) {
        try {
            waitForClickable(element);
            element.click();
        } catch (ElementClickInterceptedException e) {
            ((org.openqa.selenium.JavascriptExecutor) BaseClass.driver).executeScript("arguments[0].click();", element);
        } catch (Exception e) {
            logger.warn("Click failed: {}", e.getMessage());
        }
    }
}