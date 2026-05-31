package stepdefinitions;

import base.BaseClass;
import hooks.Hooks;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ExcelUtils;
import utils.ScreenshotUtils;

/**
 * ValidationSteps - Step Definitions for Invalid Search validation scenario.
 * Reads invalid test data from Excel ValidationData sheet.
 * Step definitions ONLY call Page Object methods.
 */
public class ValidationSteps {

    private static final Logger logger = LogManager.getLogger(ValidationSteps.class);

    private String warningMessage;
    private String screenshotPath;

    @And("User reads invalid data from Excel")
    public void user_reads_invalid_data_from_excel() {
        CommonSteps.validationData = ExcelUtils.readValidationData();
        logger.info("Excel data read - ValidationData: {}", CommonSteps.validationData);
        Hooks.logInfo("Invalid test data loaded from Excel: " + CommonSteps.validationData);
    }

    @When("User performs invalid search")
    public void user_performs_invalid_search() {
        // Read invalid data from Excel and perform search with invalid values
        String destination = CommonSteps.validationData.getOrDefault("Destination", "");
        String checkIn = CommonSteps.validationData.get("CheckInDate");
        String checkOut = CommonSteps.validationData.get("CheckOutDate");
        int adults = ExcelUtils.getIntValue(CommonSteps.validationData.get("Adults"), 2);
        int children = ExcelUtils.getIntValue(CommonSteps.validationData.get("Children"), 0);
        int rooms = ExcelUtils.getIntValue(CommonSteps.validationData.get("Rooms"), 1);

        CommonSteps.homePage.enterDestination(destination);
        CommonSteps.homePage.setCheckInDate(checkIn);
        CommonSteps.homePage.selectCheckInDate();
        CommonSteps.homePage.setCheckOutDate(checkOut);
        CommonSteps.homePage.selectCheckOutDate();
        CommonSteps.homePage.selectAdultsCount(adults);
        CommonSteps.homePage.selectChildrenCount(children);
        CommonSteps.homePage.selectRoomCount(rooms);
        CommonSteps.homePage.clickSearch();

        logger.info("Invalid search performed using Excel validation data");
        Hooks.logInfo("Invalid search performed - TestCase: "
                + CommonSteps.validationData.getOrDefault("TestCase", "Unknown"));
    }

    @Then("Warning message should be displayed")
    public void warning_message_should_be_displayed() {
        warningMessage = CommonSteps.homePage.getValidationMessage();

        System.out.println("\n========== VALIDATION RESULT ==========");
        System.out.println("Test Case       : " + CommonSteps.validationData.get("TestCase"));
        System.out.println("Warning Message : " + warningMessage);
        System.out.println("=======================================\n");

        logger.info("Validation message captured: {}", warningMessage);
        Hooks.logInfo("Warning Message Displayed: " + warningMessage);
    }

    @And("Screenshot should be captured")
    public void screenshot_should_be_captured() {
        screenshotPath = ScreenshotUtils.captureScreenshot(BaseClass.driver, "Validation_" +
                CommonSteps.validationData.getOrDefault("TestCase", "InvalidSearch"));

        System.out.println("Screenshot saved at: " + screenshotPath);
        logger.info("Screenshot captured at: {}", screenshotPath);

        if (!screenshotPath.isEmpty()) {
            Hooks.extentTest.addScreenCaptureFromPath(screenshotPath);
            Hooks.logPass("Screenshot captured and attached to Extent Report");
        }
    }
}
