package stepdefinitions;

import base.BaseClass;
import hooks.Hooks;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pages.PropertyDetailsPage;
import pages.SearchResultsPage;
import utils.ExcelUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * SearchSteps - Step Definitions for Holiday Home Search scenario.
 * Reads test data from Excel and calls Page Object methods only.
 * No driver.findElement() used here.
 */
public class SearchSteps {

    private static final Logger logger = LogManager.getLogger(SearchSteps.class);

    private SearchResultsPage searchResultsPage;
    private PropertyDetailsPage propertyDetailsPage;

    // ArrayList to store property names as required
    private ArrayList<String> propertyNamesList = new ArrayList<>();

    @When("User reads search data from Excel")
    public void user_reads_search_data_from_excel() {
        CommonSteps.searchData = ExcelUtils.readSearchData();
        logger.info("Excel data read: {}", CommonSteps.searchData);
        Hooks.logInfo("Search data loaded from Excel: " + CommonSteps.searchData);
    }

    @And("User enters destination")
    public void user_enters_destination() {
        String destination = CommonSteps.searchData.get("Destination");
        CommonSteps.homePage.enterDestination(destination);
        Hooks.logInfo("Destination entered: " + destination);
    }

    @And("User selects check-in date")
    public void user_selects_check_in_date() {
        String checkIn = CommonSteps.searchData.get("CheckInDate");
        CommonSteps.homePage.setCheckInDate(checkIn);
        CommonSteps.homePage.selectCheckInDate();
        Hooks.logInfo("Check-in date selected: " + checkIn);
    }

    @And("User selects check-out date")
    public void user_selects_check_out_date() {
        String checkOut = CommonSteps.searchData.get("CheckOutDate");
        CommonSteps.homePage.setCheckOutDate(checkOut);
        CommonSteps.homePage.selectCheckOutDate();
        Hooks.logInfo("Check-out date selected: " + checkOut);
    }

    @And("User selects adults count")
    public void user_selects_adults_count() {
        int adults = ExcelUtils.getIntValue(CommonSteps.searchData.get("Adults"), 2);
        CommonSteps.homePage.selectAdultsCount(adults);
        Hooks.logInfo("Adults count selected: " + adults);
    }

    @And("User selects children count")
    public void user_selects_children_count() {
        int children = ExcelUtils.getIntValue(CommonSteps.searchData.get("Children"), 0);
        CommonSteps.homePage.selectChildrenCount(children);
        Hooks.logInfo("Children count selected: " + children);
    }

    @And("User selects room count")
    public void user_selects_room_count() {
        int rooms = ExcelUtils.getIntValue(CommonSteps.searchData.get("Rooms"), 1);
        CommonSteps.homePage.selectRoomCount(rooms);
        Hooks.logInfo("Room count selected: " + rooms);
    }

    @And("User performs search")
    public void user_performs_search() {
        CommonSteps.homePage.clickSearch();
        searchResultsPage = new SearchResultsPage(BaseClass.driver);
        searchResultsPage.waitForResultsPage();
        Hooks.logInfo("Search performed successfully");
    }

    @And("User sorts by highest traveler rating")
    public void user_sorts_by_highest_traveler_rating() {
        searchResultsPage.sortByHighestTravelerRating();
        searchResultsPage.scrollDownResultsPage();
        logger.info("Sorting applied - highest traveler rating");
        Hooks.logInfo("Sorted by highest traveler rating");
    }

    @Then("Display first 3 properties")
    public void display_first_3_properties() {
        searchResultsPage.extractFirstThreeProperties();
        propertyNamesList = searchResultsPage.getPropertyNames();

        String report = searchResultsPage.getPropertyDisplayReport();
        System.out.println(report);
        logger.info(report);
        Hooks.logPass("Displayed first 3 properties. Names stored in ArrayList: " + propertyNamesList);
    }

    @And("Display total amount")
    public void display_total_amount() {
        for (int i = 0; i < searchResultsPage.propertyDetails.size(); i++) {
            Map<String, String> details = searchResultsPage.propertyDetails.get(i);
            String message = "Property " + (i + 1) + " Total Amount: " + details.get("Total Amount");
            System.out.println(message);
            logger.info(message);
            Hooks.logInfo(message);
        }
    }

    @And("Display charges per night")
    public void display_charges_per_night() {
        for (int i = 0; i < searchResultsPage.propertyDetails.size(); i++) {
            Map<String, String> details = searchResultsPage.propertyDetails.get(i);
            String message = "Property " + (i + 1) + " Charges Per Night: " + details.get("Charges Per Night");
            System.out.println(message);
            logger.info(message);
            Hooks.logInfo(message);
        }
    }

    @And("Display traveler rating")
    public void display_traveler_rating() {
        for (int i = 0; i < searchResultsPage.propertyDetails.size(); i++) {
            Map<String, String> details = searchResultsPage.propertyDetails.get(i);
            String message = "Property " + (i + 1) + " Traveler Rating: " + details.get("Traveler Rating");
            System.out.println(message);
            logger.info(message);
            Hooks.logInfo(message);
        }
    }

    // ==================== SCENARIO 2 - AMENITY VERIFICATION ====================

    @And("User opens first property")
    public void user_opens_first_property() throws Exception {
        searchResultsPage.openFirstProperty();
        Hooks.logInfo("Opened first property from search results");
    }

    @Then("Elevator or Lift should be verified")
    public void elevator_or_lift_should_be_verified() {
        propertyDetailsPage = new PropertyDetailsPage(BaseClass.driver);
        propertyDetailsPage.waitForPropertyPage();
        Hooks.logInfo("Amenity verification performed on property page");
    }

    @And("Display amenity verification result")
    public void display_amenity_verification_result() {
        String result = propertyDetailsPage.getAmenityVerificationResult();
        System.out.println(result);
        logger.info(result);
        Hooks.logPass(result);
        propertyDetailsPage.navigateBackToSearchResults();
    }
}
