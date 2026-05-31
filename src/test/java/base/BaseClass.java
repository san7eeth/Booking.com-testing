package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

/**
 * BaseClass - Starting point of the framework.
 * Loads config, launches Chrome browser, and closes browser after tests.
 */
public class BaseClass {

    // WebDriver instance shared across all page classes and hooks
    public static WebDriver driver;

    // Configuration properties loaded from config.properties
    public static Properties config = new Properties();

    private static final Logger logger = LogManager.getLogger(BaseClass.class);

    /**
     * Loads config.properties file from src/test/resources/config/
     */
    public static void loadConfig() {
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config/config.properties");
            config.load(fis);
            fis.close();
            logger.info("Configuration file loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to load config.properties: {}", e.getMessage());
            throw new RuntimeException("Could not load config.properties", e);
        }
    }

    /**
     * Initializes Chrome browser using WebDriverManager.
     * WebDriverManager automatically downloads the correct ChromeDriver version.
     */
    public static WebDriver initBrowser() {
        loadConfig();

        logger.info("Launching {} browser", config.getProperty("browser"));

        // Setup ChromeDriver automatically
        WebDriverManager.chromedriver().setup();

        // Chrome options to reduce popups and improve stability
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        driver = new ChromeDriver(options);

        // Set implicit wait from config file
        int implicitWait = Integer.parseInt(config.getProperty("implicitWait", "10"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        logger.info("Browser launched successfully");
        return driver;
    }

    /**
     * Opens the Booking.com homepage using URL from config file.
     */
    public static void openBookingHomePage() {
        String url = config.getProperty("baseUrl");
        logger.info("Navigating to Booking.com: {}", url);
        driver.get(url);
    }

    /**
     * Opens the Cruise information website using URL from config file.
     */
    public static void openCruiseWebsite() {
        String url = config.getProperty("cruiseUrl");
        logger.info("Navigating to Cruise Website: {}", url);
        driver.get(url);
    }

    /**
     * Closes the browser after test execution.
     */
    public static void closeBrowser() {
        if (driver != null) {
            logger.info("Closing browser");
            driver.quit();
            driver = null;
        }
    }
}
