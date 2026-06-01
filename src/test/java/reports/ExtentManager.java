package reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

    private static ExtentReports extentReports;
    private static boolean isInitialized = false;

    public static void initReports() {
        if (!isInitialized) {
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/ExtentReport.html");
            sparkReporter.config().setDocumentTitle("Holiday Home Automation");
            sparkReporter.config().setReportName("Test Report");
            sparkReporter.config().setTheme(Theme.STANDARD);

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("Framework", "Selenium + Cucumber + TestNG + Extent");

            isInitialized = true;
        }
    }

    public static ExtentTest createTest(String testName) {
        return extentReports.createTest(testName);
    }

    public static void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}