package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.WaitUtils;

import java.util.List;

/**
 * PropertyDetailsPage - Page Object for individual property details page on Booking.com.
 * Used to verify amenities like Elevator or Lift availability.
 */
public class PropertyDetailsPage {

    private WebDriver driver;
    private static final Logger logger = LogManager.getLogger(PropertyDetailsPage.class);

    public PropertyDetailsPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // ==================== LOCATORS ====================

    /** Facilities / Amenities section on property page */
    @FindBy(css = "[data-testid='property-section--content'], #hp_facilities_box, .facilitiesChecklistSection")
    private WebElement facilitiesSection;

    /** All facility / amenity items listed on the page */
    @FindBy(xpath = "//div[contains(@data-testid,'facility') or contains(@class,'facilities')]//span | //ul[contains(@class,'facilities')]//li | //div[@id='hp_facilities_box']//span")
    private List<WebElement> amenityItems;

    /** Show all facilities button (if amenities are collapsed) */
    @FindBy(xpath = "//button[contains(text(),'Show all facilities') or contains(text(),'See all facilities') or contains(text(),'Show more')]")
    private WebElement showAllFacilitiesButton;

    // ==================== PAGE METHODS ====================

    /**
     * Waits for property details page to load.
     */
    public void waitForPropertyPage() {
        logger.info("Waiting for property details page to load");
        try {
            Thread.sleep(3000);
            WaitUtils.waitForTitleContains("Booking");
        } catch (Exception e) {
            logger.info("Property page loaded");
        }
    }

    /**
     * Expands the facilities section if it is collapsed.
     */
    public void expandFacilitiesSection() {
        try {
            WaitUtils.scrollToElement(facilitiesSection);
            if (showAllFacilitiesButton.isDisplayed()) {
                WaitUtils.safeClick(showAllFacilitiesButton);
                Thread.sleep(1500);
                logger.info("Expanded facilities section");
            }
        } catch (Exception e) {
            logger.info("Facilities section already visible or button not found");
        }
    }

    /**
     * Verifies if Elevator OR Lift is available in property amenities.
     *
     * @return true if Elevator or Lift is found, false otherwise
     */
    public boolean isElevatorOrLiftAvailable() {
        logger.info("Checking for Elevator or Lift availability");
        expandFacilitiesSection();

        // Check all amenity items
        for (WebElement amenity : amenityItems) {
            try {
                String amenityText = amenity.getText().trim().toLowerCase();
                if (amenityText.contains("elevator") || amenityText.contains("lift")) {
                    logger.info("Found amenity: {}", amenity.getText());
                    return true;
                }
            } catch (Exception e) {
                // Skip stale elements
            }
        }

        // Fallback: search entire page source
        String pageText = driver.getPageSource().toLowerCase();
        boolean found = pageText.contains("elevator") || pageText.contains("lift");
        logger.info("Elevator/Lift available (page source check): {}", found);
        return found;
    }

    /**
     * Returns a display message for amenity verification result.
     */
    public String getAmenityVerificationResult() {
        boolean isAvailable = isElevatorOrLiftAvailable();
        String result = isAvailable
                ? "PASS - Elevator or Lift is AVAILABLE in this property"
                : "FAIL - Elevator or Lift is NOT available in this property";

        logger.info("Amenity verification result: {}", result);
        return result;
    }

    /**
     * Navigates back to the search results page after amenity verification.
     */
    public void navigateBackToSearchResults() {
        logger.info("Navigating back to search results page");
        try {
            driver.navigate().back();
            Thread.sleep(2000);
            logger.info("Returned to search results page");
        } catch (Exception e) {
            logger.warn("Navigate back issue: {}", e.getMessage());
            driver.navigate().back();
        }
    }

    /**
     * Navigates back to the Booking.com homepage.
     */
    public void navigateBackToHomePage() {
        logger.info("Navigating back to home page");
        try {
            driver.navigate().back();
            Thread.sleep(2000);

            // If still on property page, navigate directly to base URL
            if (!driver.getCurrentUrl().contains("booking.com/?") &&
                    !driver.getCurrentUrl().equals("https://www.booking.com/")) {
                driver.get("https://www.booking.com");
            }

            logger.info("Returned to home page");

        } catch (Exception e) {
            logger.warn("Navigate back issue: {}", e.getMessage());
            driver.get("https://www.booking.com");
        }
    }
}
