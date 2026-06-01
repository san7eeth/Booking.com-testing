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

public class BaseClass {

    public static WebDriver driver;
    public static Properties config = new Properties();
    private static final Logger logger = LogManager.getLogger(BaseClass.class);

    public static void loadConfig() {
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config/config.properties");
            config.load(fis);
            fis.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not load config.properties", e);
        }
    }

    public static WebDriver initBrowser() {
        loadConfig();
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized", "--disable-notifications", "--disable-popup-blocking");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        logger.info("Browser launched successfully");

        // Reuse session across scenarios, close on JVM exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                closeBrowser();
            } catch (Exception ignored) {
            }
        }));

        return driver;
    }

    public static void openBookingHomePage() {
        driver.get(config.getProperty("baseUrl"));
    }

    public static void closeBrowser() {
        if (driver != null) {
            logger.info("Browser closed");
            driver.quit();
            driver = null;
        }
    }
}