package stepdefinitions;

import base.BaseClass;
import hooks.Hooks;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pages.HomePage;
import utils.ExcelUtils;

import java.util.HashMap;

/**
 * CommonSteps - Shared step definitions used across multiple feature files.
 * Stores Excel test data in HashMap for use by other step definition classes.
 */
public class CommonSteps {

    private static final Logger logger = LogManager.getLogger(CommonSteps.class);

    // Shared Page Object instance
    public static HomePage homePage;

    // Excel test data stored here and shared across steps
    public static HashMap<String, String> searchData = new HashMap<>();
    public static HashMap<String, String> validationData = new HashMap<>();

    @Given("User launches Booking.com")
    public void user_launches_booking_com() {
        BaseClass.openBookingHomePage();
        homePage = new HomePage(BaseClass.driver);
        logger.info("Browser launch - Booking.com opened");
        Hooks.logInfo("Booking.com launched successfully");
    }

    @And("User closes popup if displayed")
    public void user_closes_popup_if_displayed() {
        homePage.closePopupsIfDisplayed();
        logger.info("Popup closed (if displayed)");
        Hooks.logInfo("Popups closed if displayed");
    }
}
