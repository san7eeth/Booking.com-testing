package utils;

import base.BaseClass;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ScreenshotUtils - Captures screenshots on test failure or validation steps.
 */
public class ScreenshotUtils {

    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "test-output/screenshots/";

    /**
     * Captures a screenshot and saves it to test-output/screenshots/ folder.
     *
     * @param driver   WebDriver instance
     * @param testName Name used in the screenshot file name
     * @return Full path of the saved screenshot file
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        String screenshotPath = "";

        try {
            // Create screenshots folder if it does not exist
            File directory = new File(SCREENSHOT_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate unique file name with timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = testName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";
            screenshotPath = SCREENSHOT_DIR + fileName;

            // Capture screenshot
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            File destinationFile = new File(screenshotPath);

            FileUtils.copyFile(sourceFile, destinationFile);
            logger.info("Screenshot saved at: {}", screenshotPath);

        } catch (IOException e) {
            logger.error("Failed to capture screenshot: {}", e.getMessage());
        }

        return screenshotPath;
    }
}
