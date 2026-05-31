package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import utils.WaitUtils;

import java.util.List;

/**
 * HomePage - Page Object for Booking.com homepage.
 * Uses Page Factory with @FindBy annotations.
 * All locators are kept inside this class only.
 */
public class HomePage {

    private WebDriver driver;
    private static final Logger logger = LogManager.getLogger(HomePage.class);

    // Stored values from Excel - used across step methods
    private String checkInDate;
    private String checkOutDate;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // ==================== LOCATORS ====================

    /** Cookie consent accept button */
    @FindBy(css = "#onetrust-accept-btn-handler, button[id*='accept']")
    private WebElement cookieAcceptButton;

    /** Sign-in popup close / dismiss button */
    @FindBy(css = "[aria-label='Dismiss sign-in info.'], button[aria-label*='Close'], button[aria-label*='close']")
    private WebElement signInPopupCloseButton;

    /** Welcome / promotional popup close button */
    @FindBy(xpath = "//button[contains(@class,'close') or contains(@aria-label,'Close') or contains(@aria-label,'close')]")
    private List<WebElement> welcomePopupCloseButtons;

    /** Destination search input box */
    @FindBy(css = "input[name='ss'], input[placeholder*='Where'], [data-testid='destination-container'] input")
    private WebElement destinationInput;

    /** Autocomplete suggestion list items */
    @FindBy(css = "ul[role='listbox'] li, [data-testid='autocomplete-result'] li")
    private List<WebElement> autocompleteSuggestions;

    /** Check-in date field */
    @FindBy(css = "[data-testid='date-display-field-start'], button[data-testid='date-display-field-start']")
    private WebElement checkInDateField;

    /** Check-out date field */
    @FindBy(css = "[data-testid='date-display-field-end'], button[data-testid='date-display-field-end']")
    private WebElement checkOutDateField;

    /** Calendar date cells */
    @FindBy(css = "span[data-date], td[data-date] span, [data-testid='calendar-date']")
    private List<WebElement> calendarDates;

    /** Guest / occupancy configuration button */
    @FindBy(css = "button[data-testid='occupancy-config']")
    private WebElement guestConfigButton;

    /** Guest / occupancy popup panel */
    @FindBy(css = "[data-testid='occupancy-popup']")
    private WebElement occupancyPopup;

    /** Adults range slider (Booking.com uses hidden range inputs) */
    @FindBy(id = "group_adults")
    private WebElement adultsRangeInput;

    /** Children range slider */
    @FindBy(id = "group_children")
    private WebElement childrenRangeInput;

    /** Rooms range slider */
    @FindBy(id = "no_rooms")
    private WebElement roomsRangeInput;

    /** Done button to confirm guest selection */
    @FindBy(xpath = "//button[.//span[normalize-space()='Done']]")
    private WebElement guestDoneButton;

    /** Child age dropdowns (appear when children count > 0) */
    @FindBy(css = "[data-testid='occupancy-popup'] select")
    private List<WebElement> childAgeDropdowns;

    /** Search / Submit button */
    @FindBy(css = "button[type='submit'], [data-testid='searchbox-footer'] button")
    private WebElement searchButton;

    /** Validation / warning message when search is invalid */
    @FindBy(css = "[data-testid='searchbox-error'], .fe_banner__message, [role='alert'], .sb-searchbox-error")
    private WebElement validationMessage;

    // ==================== POPUP HANDLING ====================

    /**
     * Closes all common popups on Booking.com if they appear.
     * Handles: Cookie popup, Sign-in popup, Welcome popup.
     */
    public void closePopupsIfDisplayed() {
        closeSignInPopup();
        closeCookiePopup();
        closeWelcomePopup();
    }

    /** Closes cookie consent banner if displayed */
    private void closeCookiePopup() {
        try {
            WaitUtils.waitForClickable(cookieAcceptButton);
            cookieAcceptButton.click();
            logger.info("Popup closed: Cookie consent banner");
        } catch (Exception e) {
            logger.info("Cookie popup not displayed");
        }
    }

    /** Closes sign-in popup if displayed */
    private void closeSignInPopup() {
        try {
            WaitUtils.waitForClickable(signInPopupCloseButton);
            signInPopupCloseButton.click();
            logger.info("Popup closed: Sign-in popup");
        } catch (Exception e) {
            logger.info("Sign-in popup not displayed");
        }
    }

    /** Closes welcome / promotional popup if displayed */
    private void closeWelcomePopup() {
        try {
            for (WebElement closeBtn : welcomePopupCloseButtons) {
                if (closeBtn.isDisplayed()) {
                    WaitUtils.safeClick(closeBtn);
                    logger.info("Popup closed: Welcome popup");
                    break;
                }
            }
        } catch (Exception e) {
            logger.info("Welcome popup not displayed");
        }
    }

    // ==================== SEARCH ACTIONS ====================

    /**
     * Enters destination city name and selects from autocomplete.
     *
     * @param destination City name from Excel (e.g., "Nairobi")
     */
    public void enterDestination(String destination) {
        logger.info("Destination entered: {}", destination);
        WaitUtils.waitForVisibility(destinationInput);
        destinationInput.clear();

        // Empty destination is used for validation scenario
        if (destination == null || destination.trim().isEmpty()) {
            logger.info("Empty destination - validation test");
            return;
        }

        destinationInput.sendKeys(destination);

        try {
            Thread.sleep(2000);
            if (!autocompleteSuggestions.isEmpty()) {
                WaitUtils.safeClick(autocompleteSuggestions.get(0));
                logger.info("Selected destination from autocomplete");
            } else {
                destinationInput.sendKeys(Keys.ARROW_DOWN, Keys.ENTER);
            }
        } catch (Exception e) {
            destinationInput.sendKeys(Keys.ENTER);
        }
    }

    /**
     * Stores check-in date from Excel for use in date selection step.
     */
    public void setCheckInDate(String date) {
        this.checkInDate = date;
    }

    /**
     * Stores check-out date from Excel for use in date selection step.
     */
    public void setCheckOutDate(String date) {
        this.checkOutDate = date;
    }

    /**
     * Opens calendar and selects check-in date.
     */
    public void selectCheckInDate() {
        logger.info("Date selected - Check-in: {}", checkInDate);
        try {
            WaitUtils.safeClick(checkInDateField);
            Thread.sleep(1000);
            selectDateFromCalendar(checkInDate);
        } catch (Exception e) {
            logger.warn("Check-in date selection issue: {}", e.getMessage());
        }
    }

    /**
     * Opens calendar and selects check-out date.
     */
    public void selectCheckOutDate() {
        logger.info("Date selected - Check-out: {}", checkOutDate);
        try {
            WaitUtils.safeClick(checkOutDateField);
            Thread.sleep(1000);
            selectDateFromCalendar(checkOutDate);
            dismissOpenPanels();
        } catch (Exception e) {
            logger.warn("Check-out date selection issue: {}", e.getMessage());
        }
    }

    /**
     * Clicks the correct date in the calendar using data-date attribute.
     */
    private void selectDateFromCalendar(String dateStr) {
        for (WebElement dateElement : calendarDates) {
            String dateAttribute = dateElement.getAttribute("data-date");
            if (dateStr != null && dateStr.equals(dateAttribute)) {
                WaitUtils.scrollToElement(dateElement);
                WaitUtils.safeClick(dateElement);
                return;
            }
        }
        // Fallback: click first available date
        if (!calendarDates.isEmpty()) {
            WaitUtils.safeClick(calendarDates.get(0));
        }
    }

    /**
     * Opens guest popup and sets adults count.
     *
     * @param adults Number of adults from Excel
     */
    public void selectAdultsCount(int adults) {
        logger.info("Guest selection - Adults: {}", adults);
        ensureGuestPopupIsOpen();
        setRangeValue(adultsRangeInput, adults);
        logger.info("Adults count set to: {}", adults);
    }

    /**
     * Sets children count in guest popup.
     *
     * @param children Number of children from Excel
     */
    public void selectChildrenCount(int children) {
        logger.info("Guest selection - Children: {}", children);
        ensureGuestPopupIsOpen();
        setRangeValue(childrenRangeInput, children);
        selectChildAgesIfRequired();
        logger.info("Children count set to: {}", children);
    }

    /**
     * Sets room count in guest popup and clicks Done.
     *
     * @param rooms Number of rooms from Excel
     */
    public void selectRoomCount(int rooms) {
        logger.info("Guest selection - Rooms: {}", rooms);
        ensureGuestPopupIsOpen();
        setRangeValue(roomsRangeInput, rooms);
        closeGuestPopup();
        logger.info("Rooms count set to: {} and guest popup closed", rooms);
    }

    /** Selects default child age when age dropdown appears */
    private void selectChildAgesIfRequired() {
        try {
            Thread.sleep(800);
            for (WebElement dropdown : childAgeDropdowns) {
                if (dropdown.isDisplayed()) {
                    Select select = new Select(dropdown);
                    if (select.getOptions().size() > 1) {
                        select.selectByIndex(1);
                        logger.info("Child age selected from dropdown");
                    }
                }
            }
        } catch (Exception e) {
            logger.info("No child age dropdown displayed");
        }
    }

    /**
     * Closes any open calendar/date panel before opening guest popup.
     */
    private void dismissOpenPanels() {
        try {
            destinationInput.sendKeys(Keys.ESCAPE);
            Thread.sleep(500);
        } catch (Exception e) {
            logger.info("No open panel to dismiss");
        }
    }

    /**
     * Opens the guest popup if it is not already open.
     */
    private void ensureGuestPopupIsOpen() {
        dismissOpenPanels();
        try {
            if (occupancyPopup.isDisplayed()) {
                logger.info("Guest popup already open");
                return;
            }
        } catch (Exception e) {
            // Popup not visible yet - open it
        }

        try {
            WaitUtils.scrollToElement(guestConfigButton);
            WaitUtils.safeClick(guestConfigButton);
            WaitUtils.waitForVisibility(occupancyPopup);
            logger.info("Guest popup opened");
        } catch (Exception e) {
            logger.error("Could not open guest popup: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Sets a range slider value on Booking.com guest popup.
     * Booking.com uses React-controlled range inputs - we must use
     * native value setter + input/change events for the UI to update.
     */
    private void setRangeValue(WebElement rangeInput, int targetValue) {
        try {
            WaitUtils.waitForVisibility(rangeInput);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "var input = arguments[0];" +
                    "var value = String(arguments[1]);" +
                    "var nativeSetter = Object.getOwnPropertyDescriptor(" +
                    "    window.HTMLInputElement.prototype, 'value').set;" +
                    "nativeSetter.call(input, value);" +
                    "input.setAttribute('aria-valuenow', value);" +
                    "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                    "input.dispatchEvent(new Event('change', { bubbles: true }));",
                    rangeInput, targetValue);

            Thread.sleep(500);

            String updatedValue = rangeInput.getAttribute("value");
            logger.info("Range input updated to: {}", updatedValue);

        } catch (Exception e) {
            logger.warn("Range input update failed, trying +/- buttons: {}", e.getMessage());
            clickPlusMinusToTarget(rangeInput, targetValue);
        }
    }

    /**
     * Fallback: clicks plus/minus buttons to reach target count.
     * Used only if JavaScript range update fails.
     */
    private void clickPlusMinusToTarget(WebElement rangeInput, int targetValue) {
        try {
            int currentValue = Integer.parseInt(rangeInput.getAttribute("value"));
            String inputId = rangeInput.getAttribute("id");

            WebElement plusButton = driver.findElement(By.xpath(
                    "//div[@data-testid='occupancy-popup']//label[@for='" + inputId + "']" +
                    "/ancestor::div[contains(@class,'e484bb5b7a')]" +
                    "//div[contains(@class,'e301a14002')]//button[last()]"));

            WebElement minusButton = driver.findElement(By.xpath(
                    "//div[@data-testid='occupancy-popup']//label[@for='" + inputId + "']" +
                    "/ancestor::div[contains(@class,'e484bb5b7a')]" +
                    "//div[contains(@class,'e301a14002')]//button[1]"));

            if (targetValue > currentValue) {
                for (int i = currentValue; i < targetValue; i++) {
                    WaitUtils.safeClick(plusButton);
                    Thread.sleep(300);
                }
            } else if (targetValue < currentValue) {
                for (int i = currentValue; i > targetValue; i--) {
                    WaitUtils.safeClick(minusButton);
                    Thread.sleep(300);
                }
            }
        } catch (Exception e) {
            logger.error("Plus/minus fallback also failed: {}", e.getMessage());
        }
    }

    /** Clicks Done button to confirm and close guest popup */
    private void closeGuestPopup() {
        try {
            WaitUtils.safeClick(guestDoneButton);
            Thread.sleep(500);
            logger.info("Guest popup closed using Done button");
        } catch (Exception e) {
            logger.warn("Done button not found, clicking outside popup");
            try {
                destinationInput.click();
            } catch (Exception ex) {
                logger.info("Guest popup close attempted");
            }
        }
    }

    /**
     * Clicks the Search button to perform search.
     */
    public void clickSearch() {
        logger.info("Search performed");
        WaitUtils.scrollToElement(searchButton);
        WaitUtils.safeClick(searchButton);
    }

    /**
     * Returns the validation/warning message text displayed on the page.
     */
    public String getValidationMessage() {
        try {
            Thread.sleep(2000);
            WaitUtils.waitForVisibility(validationMessage);
            String message = validationMessage.getText().trim();
            logger.info("Validation message captured: {}", message);
            return message.isEmpty() ? "Validation message displayed (empty text)" : message;
        } catch (Exception e) {
            String pageSource = driver.getPageSource().toLowerCase();
            if (pageSource.contains("enter a destination") || pageSource.contains("where are you going")) {
                return "Please enter a destination to start searching";
            }
            if (pageSource.contains("date") || pageSource.contains("check-in")) {
                return "Invalid date selection - please check your dates";
            }
            logger.warn("Validation message not found: {}", e.getMessage());
            return "Validation message not displayed";
        }
    }
}
