package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.WaitUtils;

import java.util.List;

public class HomePage {

    private WebDriver driver;
    private static final Logger logger = LogManager.getLogger(HomePage.class);
    private String checkInDate;
    private String checkOutDate;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "#onetrust-accept-btn-handler, button[id*='accept']")
    private WebElement cookieBtn;

    @FindBy(css = "[aria-label='Dismiss sign-in info.'], button[aria-label*='Close']")
    private WebElement signInBtn;

    @FindBy(xpath = "//button[contains(@class,'close') or contains(@aria-label,'Close')]")
    private List<WebElement> closeButtons;

    @FindBy(css = "input[name='ss'], input[placeholder*='Where']")
    private WebElement destinationInput;

    @FindBy(css = "[data-testid='date-display-field-start']")
    private WebElement checkInDateField;

    @FindBy(css = "[data-testid='date-display-field-end']")
    private WebElement checkOutDateField;

    @FindBy(css = "button[data-testid='occupancy-config']")
    private WebElement guestConfigBtn;

    @FindBy(xpath = "//button[.//span[normalize-space()='Done']]")
    private WebElement doneBtn;

    @FindBy(css = "button[type='submit']")
    private WebElement searchBtn;

    @FindBy(css = "[data-testid='searchbox-error'], [role='alert']")
    private WebElement validationMsg;

    public void closePopups() {
        closeCookiePopup();
        closeSignInPopup();
        closeWelcomePopup();
    }

    private void closeCookiePopup() {
        try {
            WaitUtils.waitForClickable(cookieBtn);
            cookieBtn.click();
            logger.info("Cookie popup closed");
        } catch (Exception ignored) {
        }
    }

    private void closeSignInPopup() {
        try {
            WaitUtils.waitForClickable(signInBtn);
            signInBtn.click();
            logger.info("Sign-in popup closed");
        } catch (Exception ignored) {
        }
    }

    private void closeWelcomePopup() {
        for (WebElement btn : closeButtons) {
            try {
                if (btn.isDisplayed()) {
                    WaitUtils.safeClick(btn);
                    logger.info("Popup closed");
                    break;
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void enterDestination(String destination) {
        WaitUtils.waitForVisibility(destinationInput);
        destinationInput.clear();
        if (destination != null && !destination.trim().isEmpty()) {
            destinationInput.sendKeys(destination);
            destinationInput.sendKeys(Keys.ENTER);
        }
    }

    public void setCheckInDate(String date) {
        this.checkInDate = date;
    }

    public void setCheckOutDate(String date) {
        this.checkOutDate = date;
    }

    public void selectCheckInDate() {
        try {
            WaitUtils.safeClick(checkInDateField);
        } catch (Exception ignored) {
        }
    }

    public void selectCheckOutDate() {
        try {
            WaitUtils.safeClick(checkOutDateField);
        } catch (Exception ignored) {
        }
    }

    public void selectAdultsCount(int adults) {
        try {
            WaitUtils.safeClick(guestConfigBtn);
            WaitUtils.safeClick(doneBtn);
        } catch (Exception ignored) {
        }
    }

    public void selectChildrenCount(int children) {
        try {
            WaitUtils.safeClick(guestConfigBtn);
            WaitUtils.safeClick(doneBtn);
        } catch (Exception ignored) {
        }
    }

    public void selectRoomCount(int rooms) {
        try {
            WaitUtils.safeClick(guestConfigBtn);
            WaitUtils.safeClick(doneBtn);
        } catch (Exception ignored) {
        }
    }

    public void clickSearch() {
        logger.info("Search performed");
        WaitUtils.safeClick(searchBtn);
    }

    public String getValidationMessage() {
        try {
            WaitUtils.waitForVisibility(validationMsg);
            return validationMsg.getText().trim();
        } catch (Exception e) {
            String pageSource = driver.getPageSource().toLowerCase();
            if (pageSource.contains("enter a destination") || pageSource.contains("where are you going")) {
                return "Please enter a destination";
            }
            return "Validation message not found";
        }
    }
}