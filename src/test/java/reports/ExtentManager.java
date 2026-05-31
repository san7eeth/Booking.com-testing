package reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * ExtentManager - Sets up and manages Extent HTML reports.
 * Simple static methods - easy to explain in interviews.
 */
public class ExtentManager {

    private static ExtentReports extentReports;
    private static boolean isInitialized = false;

    /**
     * Initializes Extent Reports with HTML output.
     * Called once at the start of test execution from Hooks.
     */
    public static void initReports() {
        if (!isInitialized) {
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/ExtentReport.html");
            sparkReporter.config().setDocumentTitle("Holiday Home Calculator - Test Report");
            sparkReporter.config().setReportName("Cognizant Hackathon Automation Report");
            sparkReporter.config().setTheme(Theme.STANDARD);

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("Project", "Holiday Home Cost Calculator");
            extentReports.setSystemInfo("Website", "Booking.com");
            extentReports.setSystemInfo("Framework", "Selenium + Cucumber + TestNG + POM");

            isInitialized = true;
        }
    }

    /**
     * Creates a new test entry in the Extent report for each scenario.
     */
    public static ExtentTest createTest(String testName) {
        return extentReports.createTest(testName);
    }

    /**
     * Writes all report data to the HTML file.
     * Called after each scenario from Hooks.
     */
    public static void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}
