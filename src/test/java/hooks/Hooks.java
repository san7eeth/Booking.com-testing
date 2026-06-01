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

public class Hooks {

    private static final Logger logger = LogManager.getLogger(Hooks.class);
    public static ExtentTest extentTest;

    @Before
    public void setUp(Scenario scenario) {
        if (BaseClass.driver == null) {
            BaseClass.initBrowser();
        }
        ExtentManager.initReports();
        extentTest = ExtentManager.createTest(scenario.getName());
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                String screenshotPath = ScreenshotUtils.captureScreenshot(BaseClass.driver, scenario.getName());
                if (!screenshotPath.isEmpty()) {
                    extentTest.fail("Failed").addScreenCaptureFromPath(screenshotPath);
                } else {
                    extentTest.fail("Failed - No screenshot");
                }
            } else {
                extentTest.pass("Passed");
            }
        } catch (Exception e) {
            logger.error("Error in tearDown: {}", e.getMessage());
        } finally {
            ExtentManager.flushReports();
        }
    }

    public static void logPass(String message) {
        if (extentTest != null) {
            extentTest.log(Status.PASS, message);
        }
    }

    public static void logInfo(String message) {
        if (extentTest != null) {
            extentTest.log(Status.INFO, message);
        }
    }
}