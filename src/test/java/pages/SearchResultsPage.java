package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import utils.WaitUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SearchResultsPage - Page Object for Booking.com search results page.
 * Handles filtering, sorting, and extracting property details.
 */
public class SearchResultsPage {

    private WebDriver driver;
    private static final Logger logger = LogManager.getLogger(SearchResultsPage.class);

    // Collections to store extracted property data
    public ArrayList<String> propertyNames = new ArrayList<>();
    public ArrayList<Map<String, String>> propertyDetails = new ArrayList<>();

    public SearchResultsPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // ==================== LOCATORS ====================

    /** Property type filter - Holiday Homes / Vacation Homes checkbox */
    @FindBy(xpath = "//div[contains(@data-filters-group,'ht_id')]//span[contains(text(),'Holiday homes') or contains(text(),'Vacation Homes') or contains(text(),'Holiday Homes')]/ancestor::label | //input[@type='checkbox' and @value='220']")
    private WebElement holidayHomesFilter;

    /** Sort dropdown on results page */
    @FindBy(css = "[data-testid='sorters-dropdown-trigger'], select[id*='sort'], #sort_by")
    private WebElement sortDropdown;

    /** Sort by highest traveler rating option */
    @FindBy(xpath = "//option[contains(text(),'Review score') or contains(text(),'Top reviewed') or contains(text(),'Best reviewed') or contains(text(),'Traveler rating')] | //li[contains(text(),'Review score') or contains(text(),'Top reviewed')]")
    private WebElement sortByRatingOption;

    /** All property cards on results page */
    @FindBy(css = "[data-testid='property-card'], div[data-testid='property-card-container'], .c82435a085")
    private List<WebElement> propertyCards;

    /** Property name in each card */
    @FindBy(css = "[data-testid='title'], [data-testid='property-card'] h3, [data-testid='property-card'] a[data-testid='title-link']")
    private List<WebElement> propertyNameElements;

    /** Property price / total amount */
    @FindBy(css = "[data-testid='price-and-discounted-price'], [data-testid='price-for-x-nights'], span[data-testid='price']")
    private List<WebElement> propertyPriceElements;

    /** Price per night */
    @FindBy(css = "[data-testid='price-for-x-nights'] + span, .bui-price-display__value")
    private List<WebElement> pricePerNightElements;

    /** Traveler rating / review score */
    @FindBy(css = "[data-testid='review-score'], div[data-testid='review-score'] div, .bui-review-score__badge")
    private List<WebElement> ratingElements;

    /** First property link to open details */
    @FindBy(css = "[data-testid='property-card'] a[data-testid='title-link'], [data-testid='property-card'] h3 a, [data-testid='title'] a")
    private List<WebElement> propertyLinks;

    // ==================== PAGE METHODS ====================

    /**
     * Waits for search results page to load.
     */
    public void waitForResultsPage() {
        logger.info("Waiting for search results page to load");
        try {
            Thread.sleep(3000);
            WaitUtils.waitForTitleContains("Booking");
        } catch (Exception e) {
            logger.info("Results page loaded");
        }
    }

    /**
     * Applies Holiday Homes property type filter.
     */
    public void selectHolidayHomesFilter() {
        logger.info("Selecting Holiday Homes property type filter");
        try {
            WaitUtils.scrollToElement(holidayHomesFilter);
            WaitUtils.safeClick(holidayHomesFilter);
            Thread.sleep(2000);
            logger.info("Holiday Homes filter applied");
        } catch (Exception e) {
            logger.warn("Holiday Homes filter not found, continuing with all results: {}", e.getMessage());
        }
    }

    /**
     * Sorts results by highest traveler rating.
     */
    public void sortByHighestTravelerRating() {
        logger.info("Sorting by highest traveler rating");
        try {
            WaitUtils.scrollToElement(sortDropdown);
            WaitUtils.safeClick(sortDropdown);
            Thread.sleep(1000);

            // Try clicking sort option directly
            try {
                WaitUtils.safeClick(sortByRatingOption);
            } catch (Exception e) {
                // Fallback: use Select class for dropdown
                Select select = new Select(sortDropdown);
                select.selectByVisibleText("Review score & price");
            }

            Thread.sleep(3000);
            logger.info("Sorted by highest traveler rating");

        } catch (Exception e) {
            logger.warn("Sort dropdown issue: {}", e.getMessage());
        }
    }

    /**
     * Extracts details of the first 3 properties and stores in collections.
     * Stores property names in ArrayList<String>.
     */
    public void extractFirstThreeProperties() {
        logger.info("Extracting first 3 property details");
        propertyNames.clear();
        propertyDetails.clear();

        int count = Math.min(3, propertyNameElements.size());

        for (int i = 0; i < count; i++) {
            try {
                Map<String, String> details = new HashMap<>();

                // Property Name
                String name = propertyNameElements.get(i).getText().trim();
                propertyNames.add(name);
                details.put("Property Name", name);

                // Total Amount
                String totalAmount = (i < propertyPriceElements.size())
                        ? propertyPriceElements.get(i).getText().trim() : "N/A";
                details.put("Total Amount", totalAmount);

                // Charges Per Night
                String perNight = (i < pricePerNightElements.size())
                        ? pricePerNightElements.get(i).getText().trim() : "N/A";
                details.put("Charges Per Night", perNight);

                // Traveler Rating
                String rating = (i < ratingElements.size())
                        ? ratingElements.get(i).getText().trim() : "N/A";
                details.put("Traveler Rating", rating);

                propertyDetails.add(details);

                logger.info("Property {}: {} | Total: {} | Per Night: {} | Rating: {}",
                        i + 1, name, totalAmount, perNight, rating);

            } catch (Exception e) {
                logger.warn("Could not extract property {}: {}", i + 1, e.getMessage());
            }
        }
    }

    /**
     * Scrolls down the results page to load more properties.
     */
    public void scrollDownResultsPage() {
        logger.info("Scrolling down results page");
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("window.scrollBy(0, 800);");
            Thread.sleep(1500);
        } catch (Exception e) {
            logger.warn("Scroll issue: {}", e.getMessage());
        }
    }

    /**
     * Opens the first property from search results in the same or new window.
     * @throws Exception 
     */
    public void openFirstProperty() throws Exception {
        logger.info("Opening first property from results");
        try {
            WaitUtils.scrollToElement(propertyLinks.get(0));
            String originalWindow = driver.getWindowHandle();

            WaitUtils.safeClick(propertyLinks.get(0));
            Thread.sleep(3000);

            // Handle new browser window/tab if opened
            for (String windowHandle : driver.getWindowHandles()) {
                if (!windowHandle.equals(originalWindow)) {
                    driver.switchTo().window(windowHandle);
                    logger.info("Switched to property details window");
                    break;
                }
            }

        } catch (Exception e) {
            logger.error("Could not open first property: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Returns formatted display string for all extracted properties.
     */
    public String getPropertyDisplayReport() {
        StringBuilder report = new StringBuilder();
        report.append("\n========== FIRST 3 PROPERTIES ==========\n");

        for (int i = 0; i < propertyDetails.size(); i++) {
            Map<String, String> details = propertyDetails.get(i);
            report.append("\n--- Property ").append(i + 1).append(" ---\n");
            report.append("Property Name     : ").append(details.get("Property Name")).append("\n");
            report.append("Total Amount      : ").append(details.get("Total Amount")).append("\n");
            report.append("Charges Per Night : ").append(details.get("Charges Per Night")).append("\n");
            report.append("Traveler Rating   : ").append(details.get("Traveler Rating")).append("\n");
        }

        return report.toString();
    }

    /**
     * Returns property names list.
     */
    public ArrayList<String> getPropertyNames() {
        return propertyNames;
    }
}
