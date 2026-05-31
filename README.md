# Holiday Home Cost Calculator - Automation Framework

**Cognizant Hackathon Project | Booking.com | Excel Data-Driven Testing**

A simple, beginner-friendly Selenium automation framework using **Java 17**, **Selenium WebDriver**, **Cucumber BDD**, **TestNG**, **Page Object Model (POM)**, and **Page Factory**.

---

## Tech Stack

| Component       | Technology              |
|-----------------|-------------------------|
| Language        | Java 17                 |
| Automation      | Selenium WebDriver 4.x  |
| Build Tool      | Maven                   |
| BDD             | Cucumber                |
| Test Runner     | TestNG                  |
| Design Pattern  | Page Object Model (POM) |
| Element Finding | Page Factory (@FindBy)  |
| Test Data       | Apache POI (Excel)      |
| Reporting       | Extent Reports          |
| Logging         | Log4j2                  |
| Browser         | Chrome (WebDriverManager)|

---

## Project Structure

```
Booking.com/
├── pom.xml
├── testng.xml
├── README.md
└── src/test/
    ├── java/
    │   ├── base/BaseClass.java
    │   ├── hooks/Hooks.java
    │   ├── runners/TestRunner.java
    │   ├── pages/
    │   │   ├── HomePage.java
    │   │   ├── SearchResultsPage.java
    │   │   └── PropertyDetailsPage.java
    │   ├── stepdefinitions/
    │   │   ├── CommonSteps.java
    │   │   ├── SearchSteps.java
    │   │   └── ValidationSteps.java
    │   ├── utils/
    │   │   ├── WaitUtils.java
    │   │   ├── ScreenshotUtils.java
    │   │   └── ExcelUtils.java
    │   └── reports/ExtentManager.java
    └── resources/
        ├── config/config.properties
        ├── features/
        │   ├── search.feature
        │   └── validation.feature
        ├── testdata/testdata.xlsx
        └── log4j2.xml
```

---

## Test Scenarios

### Scenario 1 - Holiday Home Search (Excel-Driven)
1. Launch Booking.com and close popups
2. Read search data from Excel (`SearchData` sheet)
3. Enter destination, dates, adults, children, rooms
4. Search and sort by highest traveler rating
5. Display first 3 properties (Name, Total Amount, Per Night, Rating)
6. Store property names in `ArrayList<String>`

### Scenario 2 - Amenity Verification
1. Open first property from search results
2. Verify **Elevator** OR **Lift** availability
3. Display result in console and Extent Report
4. Navigate back to search results page

### Scenario 3 - Validation (Excel-Driven)
1. Read invalid data from Excel (`ValidationData` sheet)
2. Perform invalid search (empty destination / invalid dates / invalid guests)
3. Capture warning message and screenshot
4. Attach screenshot to Extent Report

---

## Excel Test Data Structure

**File:** `src/test/resources/testdata/testdata.xlsx`

### Sheet: SearchData

| Destination | CheckInDate | CheckOutDate | Adults | Children | Rooms |
|-------------|-------------|--------------|--------|----------|-------|
| Nairobi     | DYNAMIC+7   | DYNAMIC+12   | 4      | 1        | 2     |

### Sheet: ValidationData

| TestCase            | Destination | CheckInDate | CheckOutDate | Adults | Children | Rooms |
|---------------------|-------------|-------------|--------------|--------|----------|-------|
| Empty Destination   | *(empty)*   | DYNAMIC+7   | DYNAMIC+12   | 2      | 0        | 1     |
| Invalid Date Range  | Nairobi     | DYNAMIC+12  | DYNAMIC+7    | 2      | 0        | 1     |
| Invalid Guest Count | Nairobi     | DYNAMIC+7   | DYNAMIC+12   | 99     | 0        | 0     |

### Dynamic Date Format
- `DYNAMIC+7`  = Today + 7 days (used for check-in)
- `DYNAMIC+12` = Today + 12 days (used for check-out)
- `Dynamic Future Date` = Default offset (7 or 12 days)

Change test data by editing the Excel file — no code changes needed.

---

## Prerequisites

1. Java JDK 17
2. Maven 3.6+
3. Google Chrome browser

```bash
java -version
mvn -version
```

---

## How to Run

### Maven Command Line
```bash
cd "c:\QEA Testing\Booking.com\Booking.com"
mvn clean test
```

### IDE (Eclipse / IntelliJ)
1. Import as Maven project
2. Right-click `runners.TestRunner.java`
3. Run As → TestNG Test

---

## Reports & Logs

| Output             | Location                          |
|--------------------|-----------------------------------|
| Extent HTML Report | `test-output/ExtentReport.html`   |
| Cucumber Report    | `test-output/cucumber-report.html`|
| Screenshots        | `test-output/screenshots/`        |
| Automation Log     | `test-output/automation.log`      |

---

## Configuration

Edit `src/test/resources/config/config.properties`:

```properties
browser=chrome
baseUrl=https://www.booking.com
implicitWait=10
explicitWait=20
excelPath=src/test/resources/testdata/testdata.xlsx
searchSheet=SearchData
validationSheet=ValidationData
```

---

## Framework Design (Interview Explanation)

1. **POM** — Each web page = one Java class with locators and methods
2. **Page Factory** — `@FindBy` + `PageFactory.initElements(driver, this)`
3. **Cucumber BDD** — Feature files in plain English, steps call Page methods only
4. **Excel Data-Driven** — `ExcelUtils` reads test data using Apache POI
5. **Hooks** — `@Before` launches browser; `@After` captures screenshot on failure
6. **Collections** — `ArrayList<String>` for property names; `HashMap` for Excel data
7. **Explicit Waits** — `WaitUtils` handles common Selenium exceptions

---

## Key Classes

| Class               | Purpose                                      |
|---------------------|----------------------------------------------|
| `BaseClass`         | Browser launch, config load, close browser   |
| `HomePage`          | Popups, destination, dates, guests, search   |
| `SearchResultsPage` | Sort results, extract property details       |
| `PropertyDetailsPage`| Verify Elevator/Lift amenity                |
| `ExcelUtils`        | Read test data from Excel sheets             |
| `WaitUtils`         | Explicit waits, safe click, scroll           |
| `ScreenshotUtils`   | Capture screenshots                          |
| `ExtentManager`     | HTML report generation                       |
| `Hooks`             | Before/After scenario setup                  |
| `TestRunner`        | TestNG + Cucumber entry point                |

---

## Troubleshooting

| Issue                 | Solution                                      |
|-----------------------|-----------------------------------------------|
| Excel file not found  | Check path in config.properties               |
| Element not found     | Update `@FindBy` locators in Page classes     |
| Popup blocking test   | `closePopupsIfDisplayed()` handles common ones|
| ChromeDriver mismatch | WebDriverManager handles automatically        |

---

## Authors

Cognizant Hackathon Team - QEA Testing Batch
