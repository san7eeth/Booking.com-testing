package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import utils.WaitUtils;

public class PropertyDetailsPage {

    private WebDriver driver;
    private static final Logger logger = LogManager.getLogger(PropertyDetailsPage.class);

    public PropertyDetailsPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void waitForPropertyPage() {
        try {
            WaitUtils.waitForTitleContains("Booking");
        } catch (Exception e) {
            logger.warn("Page title not updated");
        }
    }

    public String getPageTitle() {
        try {
            return driver.getTitle();
        } catch (Exception e) {
            logger.warn("Could not get page title");
            return "";
        }
    }

    public void navigateBackToSearchResults() {
        try {
            driver.navigate().back();
        } catch (Exception e) {
            logger.warn("Navigate back failed");
        }
    }
}