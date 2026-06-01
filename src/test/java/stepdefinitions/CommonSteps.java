package stepdefinitions;

import base.BaseClass;
import hooks.Hooks;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pages.HomePage;

import java.util.HashMap;

public class CommonSteps {

    private static final Logger logger = LogManager.getLogger(CommonSteps.class);
    public static HomePage homePage;
    public static HashMap<String, String> searchData = new HashMap<>();
    public static HashMap<String, String> validationData = new HashMap<>();

    @Given("User launches Booking.com")
    public void launchBooking() {
        BaseClass.openBookingHomePage();
        homePage = new HomePage(BaseClass.driver);
        Hooks.logInfo("Booking.com launched");
    }

    @And("User closes popup if displayed")
    public void closePopups() {
        homePage.closePopups();
    }
}