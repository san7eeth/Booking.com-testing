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

public class ValidationSteps {

    private static final Logger logger = LogManager.getLogger(ValidationSteps.class);
    private String warningMessage;

    @And("User reads invalid data from Excel")
    public void readInvalidData() {
        CommonSteps.validationData = ExcelUtils.readValidationData();
        Hooks.logInfo("Invalid data loaded");
    }

    @When("User performs invalid search")
    public void performInvalidSearch() {
        String destination = CommonSteps.validationData.getOrDefault("Destination", "");
        CommonSteps.homePage.enterDestination(destination);
        CommonSteps.homePage.selectCheckInDate();
        CommonSteps.homePage.selectCheckOutDate();
        CommonSteps.homePage.selectAdultsCount(2);
        CommonSteps.homePage.selectChildrenCount(0);
        CommonSteps.homePage.selectRoomCount(1);
        CommonSteps.homePage.clickSearch();
    }

    @Then("Warning message should be displayed")
    public void captureWarningMessage() {
        warningMessage = CommonSteps.homePage.getValidationMessage();
        System.out.println("Warning: " + warningMessage);
        Hooks.logInfo("Message: " + warningMessage);
    }

    @And("Screenshot should be captured")
    public void captureScreenshot() {
        String screenshotPath = ScreenshotUtils.captureScreenshot(BaseClass.driver, "Validation");
        if (!screenshotPath.isEmpty()) {
            Hooks.extentTest.addScreenCaptureFromPath(screenshotPath);
            Hooks.logPass("Screenshot captured");
        }
    }
}