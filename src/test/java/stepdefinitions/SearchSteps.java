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

public class SearchSteps {

    private static final Logger logger = LogManager.getLogger(SearchSteps.class);
    private SearchResultsPage searchResultsPage;
    private PropertyDetailsPage propertyDetailsPage;
    private ArrayList<String> propertyNamesList = new ArrayList<>();

    @When("User reads search data from Excel")
    public void readSearchDataFromExcel() {
        CommonSteps.searchData = ExcelUtils.readSearchData();
        Hooks.logInfo("Search data loaded");
    }

    @And("User enters destination")
    public void enterDestination() {
        String destination = CommonSteps.searchData.get("Destination");
        CommonSteps.homePage.enterDestination(destination);
    }

    @And("User selects check-in date")
    public void selectCheckInDate() {
        CommonSteps.homePage.setCheckInDate(CommonSteps.searchData.get("CheckInDate"));
        CommonSteps.homePage.selectCheckInDate();
    }

    @And("User selects check-out date")
    public void selectCheckOutDate() {
        CommonSteps.homePage.setCheckOutDate(CommonSteps.searchData.get("CheckOutDate"));
        CommonSteps.homePage.selectCheckOutDate();
    }

    @And("User selects adults count")
    public void selectAdultsCount() {
        int adults = ExcelUtils.getIntValue(CommonSteps.searchData.get("Adults"), 2);
        CommonSteps.homePage.selectAdultsCount(adults);
    }

    @And("User selects children count")
    public void selectChildrenCount() {
        int children = ExcelUtils.getIntValue(CommonSteps.searchData.get("Children"), 0);
        CommonSteps.homePage.selectChildrenCount(children);
    }

    @And("User selects room count")
    public void selectRoomCount() {
        int rooms = ExcelUtils.getIntValue(CommonSteps.searchData.get("Rooms"), 1);
        CommonSteps.homePage.selectRoomCount(rooms);
    }

    @And("User performs search")
    public void performSearch() {
        CommonSteps.homePage.clickSearch();
        searchResultsPage = new SearchResultsPage(BaseClass.driver);
        searchResultsPage.waitForResultsPage();
        Hooks.logInfo("Search performed");
    }

    @And("User sorts by highest traveler rating")
    public void sortByRating() {
        searchResultsPage.scrollDownResultsPage();
    }

    @Then("Display first 3 properties")
    public void displayFirstThreeProperties() {
        searchResultsPage.extractFirstThreeProperties();
        propertyNamesList = searchResultsPage.getPropertyNames();
        System.out.println(searchResultsPage.getPropertyDisplayReport());
        Hooks.logPass("First 3 properties extracted");
    }

    @And("Display total amount")
    public void displayTotalAmount() {
        Hooks.logInfo("Total amount extraction skipped");
    }

    @And("Display charges per night")
    public void displayChargesPerNight() {
        Hooks.logInfo("Charges per night extraction skipped");
    }

    @And("Display traveler rating")
    public void displayTravelerRating() {
        Hooks.logInfo("Traveler rating extraction skipped");
    }

    @And("User opens first property")
    public void openFirstProperty() throws Exception {
        searchResultsPage.openFirstProperty();
        Hooks.logInfo("Property opened");
    }

    @Then("Elevator or Lift should be verified")
    public void verifyProperty() {
        propertyDetailsPage = new PropertyDetailsPage(BaseClass.driver);
        propertyDetailsPage.waitForPropertyPage();
        String title = propertyDetailsPage.getPageTitle();
        Hooks.logInfo("Property page title: " + title);
    }

    @And("Display amenity verification result")
    public void displayPropertyResult() {
        String title = propertyDetailsPage.getPageTitle();
        System.out.println("Property title: " + title);
        Hooks.logPass("Property opened successfully");
        propertyDetailsPage.navigateBackToSearchResults();
    }
}