package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.WaitUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsPage {

    private WebDriver driver;
    private static final Logger logger = LogManager.getLogger(SearchResultsPage.class);
    public ArrayList<String> propertyNames = new ArrayList<>();

    public SearchResultsPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "[data-testid='title'], [data-testid='property-card'] h3")
    private List<WebElement> propertyNameElements;

    @FindBy(css = "[data-testid='property-card'] a[data-testid='title-link']")
    private List<WebElement> propertyLinks;

    public void waitForResultsPage() {
        try {
            WaitUtils.waitForTitleContains("Booking");
        } catch (Exception e) {
            logger.warn("Page title not updated");
        }
    }

    public void extractFirstThreeProperties() {
        propertyNames.clear();
        int count = Math.min(3, propertyNameElements.size());

        for (int i = 0; i < count; i++) {
            try {
                String name = propertyNameElements.get(i).getText().trim();
                propertyNames.add(name);
            } catch (Exception e) {
                logger.warn("Could not extract property {}: {}", i + 1, e.getMessage());
            }
        }
    }

    public void scrollDownResultsPage() {
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500);");
        } catch (Exception e) {
            logger.warn("Scroll failed");
        }
    }

    public void openFirstProperty() throws Exception {
        try {
            WaitUtils.safeClick(propertyLinks.get(0));
            WaitUtils.waitForTitleContains("Booking");
        } catch (Exception e) {
            logger.error("Could not open first property");
            throw e;
        }
    }

    public String getPropertyDisplayReport() {
        StringBuilder report = new StringBuilder("\n========== FIRST 3 PROPERTIES ==========\n");
        for (int i = 0; i < propertyNames.size(); i++) {
            report.append("Property ").append(i + 1).append(": ").append(propertyNames.get(i)).append("\n");
        }
        return report.toString();
    }

    public ArrayList<String> getPropertyNames() {
        return propertyNames;
    }
}