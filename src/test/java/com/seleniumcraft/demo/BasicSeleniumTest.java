package com.seleniumcraft.demo;

import com.seleniumcraft.core.DriverContext;
import com.seleniumcraft.reporting.ExtentReportManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

/**
 * ❌ BASIC SELENIUM TEST - THE OLD WAY ❌
 * 
 * This class demonstrates the challenges and pain points of writing raw
 * Selenium tests:
 * - Verbose, boilerplate code
 * - Fragile due to timing issues
 * - Prone to StaleElementReferenceException
 * - Difficult to maintain and scale
 * - Easy to make mistakes with explicit/implicit waits
 */
@Listeners(com.seleniumcraft.reporting.ExtentTestListener.class)
public class BasicSeleniumTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        // ❌ PROBLEM: Manual setup process is error-prone
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        // Register driver with DriverContext so listener can capture screenshots
        DriverContext.setDriver(driver);

        // ❌ PROBLEM: Implicit wait is set globally and can conflict with explicit waits
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        // ❌ PROBLEM: WebDriverWait object must be created separately
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("https://the-internet.herokuapp.com");

        ExtentReportManager.logInfo("Browser initialized - Traditional Selenium approach");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            DriverContext.removeDriver();
        }
    }

    @Test
    public void testLoginFormWithBasicSelenium() throws InterruptedException {
        // ❌ PROBLEM: Thread.sleep is unreliable - too short = fail, too long = slow
        // ❌ PROBLEM: Storing WebElement reference causes StaleElementReferenceException
        // ❌ PROBLEM: 3 lines of code just to enter text - repetitive pattern

        System.out.println("❌ [BasicSelenium] Login Test - Using verbose Selenium code");

        driver.get("https://the-internet.herokuapp.com/login");
        Thread.sleep(1000);

        // ❌ PROBLEM: Manual element lookup - no retry logic
        WebElement usernameField = driver.findElement(By.id("username"));
        usernameField.clear();
        usernameField.sendKeys("tomsmith");

        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.clear();
        passwordField.sendKeys("SuperSecretPassword!");

        // ❌ PROBLEM: Storing element reference before click causes stale element issues
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        // ❌ PROBLEM: Need explicit wait after click because element may not be
        // immediately available
        Thread.sleep(2000);

        try {
            WebElement successMessage = driver.findElement(By.cssSelector(".flash.success"));
            String message = successMessage.getText();
            Assert.assertTrue(message.contains("You logged into a secure area"));
            System.out.println("✓ Login successful (but with verbose, flaky code)");
        } catch (Exception e) {
            System.out.println("✗ Login test failed - " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testCheckboxesWithBasicSelenium() throws InterruptedException {
        // ❌ PROBLEM: 5+ lines of code just to wait for elements
        // ❌ PROBLEM: Explicit WebDriverWait syntax is verbose and repetitive
        // ❌ PROBLEM: Easy to get visibility vs clickability conditions wrong

        System.out.println("❌ [BasicSelenium] Checkbox Test - Boilerplate WebDriverWait code");

        driver.get("https://the-internet.herokuapp.com/checkboxes");
        Thread.sleep(1000);

        // ❌ PROBLEM: Verbose explicit wait syntax
        java.util.List<WebElement> checkboxes = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("input[type='checkbox']")));

        Assert.assertEquals(checkboxes.size(), 2, "Should have 2 checkboxes");

        // ❌ PROBLEM: Multiple lines to check and uncheck
        if (!checkboxes.get(0).isSelected()) {
            checkboxes.get(0).click();
            Thread.sleep(500);
        }

        if (checkboxes.get(1).isSelected()) {
            checkboxes.get(1).click();
            Thread.sleep(500);
        }

        Assert.assertTrue(checkboxes.get(0).isSelected(), "First checkbox should be checked");
        Assert.assertFalse(checkboxes.get(1).isSelected(), "Second checkbox should be unchecked");

        System.out.println("✓ Checkbox test passed (but with verbose checkbox manipulation)");
    }

    @Test
    public void testDropdownWithBasicSelenium() throws InterruptedException {
        // ❌ PROBLEM: Select class requires separate import and initialization
        // ❌ PROBLEM: 3 lines just to select one option
        // ❌ PROBLEM: No built-in retry logic if element isn't immediately ready

        System.out.println("❌ [BasicSelenium] Dropdown Test - Using Select class boilerplate");

        driver.get("https://the-internet.herokuapp.com/dropdown");
        Thread.sleep(1000);

        // ❌ PROBLEM: Need to wait, find element, then wrap in Select object
        WebElement dropdownElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("dropdown")));

        // ❌ PROBLEM: Select is verbose and not very intuitive
        Select dropdown = new Select(dropdownElement);
        dropdown.selectByVisibleText("Option 1");

        Thread.sleep(500);

        WebElement selectedOption = dropdown.getFirstSelectedOption();
        Assert.assertEquals(selectedOption.getText(), "Option 1", "Option 1 should be selected");

        System.out.println("✓ Dropdown selection completed (using verbose Select wrapper)");
    }

    @Test
    public void testIframeWithBasicSelenium() throws InterruptedException {
        // ❌ PROBLEM: Manual iframe switching is error-prone
        // ❌ PROBLEM: Easy to forget switchTo().defaultContent() - breaks subsequent
        // tests
        // ❌ PROBLEM: Nested iframes require manual context tracking
        // ❌ PROBLEM: No automatic cleanup if test fails during iframe context

        System.out.println("❌ [BasicSelenium] iFrame Test - Manual switch-in/switch-out required");

        driver.get("https://the-internet.herokuapp.com/iframe");
        Thread.sleep(1000);

        // ❌ PROBLEM: Manual frame switching - easy to forget to switch back
        WebElement iframeElement = driver.findElement(By.id("mce_0_ifr"));
        driver.switchTo().frame(iframeElement);

        try {
            // ❌ PROBLEM: Working inside iframe without auto-context management
            WebElement contentArea = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("tinymce")));

            String iframeText = contentArea.getText();
            Assert.assertFalse(iframeText.isEmpty(), "iFrame content should not be empty");

            System.out.println("✓ iFrame content accessed");
        } finally {
            // ❌ PROBLEM: Must remember to switch back explicitly
            driver.switchTo().defaultContent();
        }

        System.out.println("✓ iFrame test passed (manual switch-out successful)");
    }

    @Test
    public void testDynamicLoadingWithBasicSelenium() throws InterruptedException {
        // ❌ PROBLEM: 5+ lines of verbose WebDriverWait for each element
        // ❌ PROBLEM: ExpectedConditions syntax is not intuitive
        // ❌ PROBLEM: Multiple wait objects make code cluttered

        System.out.println("❌ [BasicSelenium] Dynamic Loading Test - Verbose WebDriverWait");

        driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");
        Thread.sleep(1000);

        // ❌ PROBLEM: Find button, wait for it, then click
        WebElement startButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='button']")));
        startButton.click();

        // ❌ PROBLEM: Another verbose wait for element to appear
        WebElement loadedElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("finish")));

        String loadedText = loadedElement.getText();
        Assert.assertFalse(loadedText.isEmpty(), "Loaded element should contain text");

        System.out.println("✓ Dynamic loading test completed (with multiple verbose wait blocks)");
    }
}
