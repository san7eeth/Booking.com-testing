package hooks;

import base.BaseClass;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reports.ExtentManager;
import utils.ScreenshotUtils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

/**
 * Hooks - Cucumber lifecycle methods that run before and after each scenario.
 *
 * @Before  - Launch browser and initialize reports
 * @After   - Generate reports, capture screenshot on failure, close browser
 */
public class Hooks {

    private static final Logger logger = LogManager.getLogger(Hooks.class);

    // ExtentTest object for the current scenario - used by step definitions
    public static ExtentTest extentTest;

    /**
     * Runs before each scenario.
     * Launches browser (if not already open) and creates Extent report entry.
     */
    @Before
    public void setUp(Scenario scenario) {
        logger.info("========== Starting Scenario: {} ==========", scenario.getName());

        // Launch browser only once
        if (BaseClass.driver == null) {
            BaseClass.initBrowser();
        }

        // Initialize Extent Reports and create test entry
        ExtentManager.initReports();
        extentTest = ExtentManager.createTest(scenario.getName());
        extentTest.info("Scenario started: " + scenario.getName());
    }

    /**
     * Runs after each scenario.
     * Updates report status, captures screenshot on failure, closes browser.
     */
    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                logger.error("Scenario FAILED: {}", scenario.getName());

                // Capture screenshot on failure
                String screenshotPath = ScreenshotUtils.captureScreenshot(
                        BaseClass.driver, scenario.getName());

                if (!screenshotPath.isEmpty()) {
                    extentTest.fail("Scenario Failed")
                            .addScreenCaptureFromPath(screenshotPath);
                } else {
                    extentTest.fail("Scenario Failed - screenshot not captured");
                }

            } else {
                logger.info("Scenario PASSED: {}", scenario.getName());
                extentTest.pass("Scenario Passed");
            }

        } catch (Exception e) {
            logger.error("Error in tearDown: {}", e.getMessage());
            extentTest.skip("Scenario skipped or error during teardown");

        } finally {
            // Flush report and close browser
            ExtentManager.flushReports();
            BaseClass.closeBrowser();
            logger.info("========== Finished Scenario: {} ==========", scenario.getName());
        }
    }

    /**
     * Helper method for step definitions to log PASS status in Extent report.
     */
    public static void logPass(String message) {
        if (extentTest != null) {
            extentTest.log(Status.PASS, message);
        }
    }

    /**
     * Helper method for step definitions to log INFO in Extent report.
     */
    public static void logInfo(String message) {
        if (extentTest != null) {
            extentTest.log(Status.INFO, message);
        }
    }
}
