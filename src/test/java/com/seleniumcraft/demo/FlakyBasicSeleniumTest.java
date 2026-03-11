package com.seleniumcraft.demo;

import com.seleniumcraft.reporting.ExtentReportManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

/**
 * ❌ FLAKY SELENIUM TEST - DEMONSTRATES REAL-WORLD FAILURES ❌
 * 
 * This class intentionally demonstrates scenarios where traditional Selenium
 * FAILS:
 * - StaleElementReferenceException after DOM modifications
 * - Race conditions with dynamic content
 * - Timing issues with Thread.sleep
 * - Flaky element interactions
 * 
 * These tests are designed to FAIL to showcase why SeleniumCraft is needed.
 */
@Listeners(com.seleniumcraft.reporting.ExtentTestListener.class)
public class FlakyBasicSeleniumTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5)); // Short timeout to show failures faster

        ExtentReportManager.logInfo("Browser initialized - Traditional Selenium approach");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        ExtentReportManager.removeTest();
    }

    @Test(description = "❌ FAILS: StaleElementException when element reference becomes stale after DOM update")
    public void testStaleElementException_WillFail() {
        // ❌ PROBLEM: This test demonstrates StaleElementReferenceException
        // When you store a WebElement reference and the DOM changes, the reference
        // becomes stale

        ExtentReportManager.logInfo("Demonstrating StaleElementReferenceException issue");
        ExtentReportManager.logWarning("This test is EXPECTED to fail - showcasing traditional Selenium weakness");

        driver.get("https://the-internet.herokuapp.com/dynamic_controls");

        // Store reference to checkbox
        WebElement checkbox = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("#checkbox input")));

        ExtentReportManager.logInfo("Stored reference to checkbox element");

        // Click the Remove button - this will remove the checkbox from DOM
        WebElement removeButton = driver.findElement(By.cssSelector("#checkbox button"));
        removeButton.click();

        ExtentReportManager.logInfo("Clicked 'Remove' button - DOM will be modified");

        // Wait for removal to complete
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));

        // ❌ THIS WILL FAIL - StaleElementReferenceException
        // The stored checkbox reference is now stale because the element was removed
        try {
            ExtentReportManager.logInfo("Attempting to check if original element is displayed...");
            boolean isDisplayed = checkbox.isDisplayed(); // THROWS StaleElementReferenceException
            ExtentReportManager.logFail("Expected StaleElementException but got: " + isDisplayed);
        } catch (StaleElementReferenceException e) {
            ExtentReportManager.logInfo("✓ StaleElementReferenceException thrown as expected");
            ExtentReportManager.logFail("Traditional Selenium cannot recover from stale element references!");
            throw e; // Re-throw to fail the test
        }
    }

    @Test(description = "❌ FAILS: Race condition with dynamically loaded content")
    public void testDynamicLoadingRaceCondition_WillFail() {
        // ❌ PROBLEM: Very short implicit wait causes race condition failures
        // This simulates real-world flaky behavior due to timing issues

        ExtentReportManager.logInfo("Demonstrating race condition with dynamic content");
        ExtentReportManager.logWarning("This test is EXPECTED to fail - showcasing timing issues");

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(100)); // Very short wait

        driver.get("https://the-internet.herokuapp.com/dynamic_loading/2");

        // Click start button
        WebElement startButton = driver.findElement(By.cssSelector("#start button"));
        startButton.click();

        ExtentReportManager.logInfo("Clicked 'Start' button - content loading...");

        // ❌ THIS WILL FAIL - NoSuchElementException
        // The element is not yet present in DOM because loading takes ~5 seconds
        // With 100ms implicit wait, Selenium gives up too quickly
        try {
            WebElement finishElement = driver.findElement(By.cssSelector("#finish h4"));
            String text = finishElement.getText();
            ExtentReportManager.logFail("Unexpected success - got text: " + text);
        } catch (NoSuchElementException e) {
            ExtentReportManager.logInfo("✓ NoSuchElementException thrown as expected");
            ExtentReportManager.logFail("Traditional Selenium failed due to race condition!");
            throw e;
        }
    }

    @Test(description = "❌ FAILS: Clicking element that disappears immediately after appearing")
    public void testDisappearingElement_WillFail() {
        // ❌ PROBLEM: Element appears briefly then disappears - hard to click reliably

        ExtentReportManager.logInfo("Demonstrating issue with briefly appearing elements");
        ExtentReportManager.logWarning("This test showcases unreliable element interactions");

        driver.get("https://the-internet.herokuapp.com/dynamic_controls");

        // Enable the text input
        WebElement enableButton = driver.findElement(By.cssSelector("#input-example button"));
        enableButton.click();

        // ❌ PROBLEM: The loading indicator appears and disappears
        // Trying to interact with it is unreliable
        try {
            // Very short wait - likely to miss the loading indicator
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofMillis(500));
            WebElement loadingIndicator = shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("loading")));

            // Try to get text while it's still visible
            String loadingText = loadingIndicator.getText();
            ExtentReportManager.logInfo("Loading indicator text: " + loadingText);

            // Now it might be gone - try to interact again
            Thread.sleep(3000); // Wait for loading to finish

            // ❌ This might throw StaleElementException or element not visible
            Assert.assertTrue(loadingIndicator.isDisplayed(),
                    "Loading indicator should still be visible");

        } catch (TimeoutException | StaleElementReferenceException e) {
            ExtentReportManager.logInfo("Element interaction failed: " + e.getClass().getSimpleName());
            ExtentReportManager.logFail("Traditional Selenium struggles with transient elements!");
            throw new AssertionError("Failed to reliably interact with transient element", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Test(description = "❌ FAILS: Multiple rapid clicks cause stale element issues")
    public void testRapidClicksStaleElement_WillFail() {
        // ❌ PROBLEM: Rapid interactions can cause stale references

        ExtentReportManager.logInfo("Demonstrating stale element issues with rapid interactions");

        driver.get("https://the-internet.herokuapp.com/add_remove_elements/");

        // Store button reference
        WebElement addButton = driver.findElement(By.cssSelector("button[onclick='addElement()']"));

        ExtentReportManager.logInfo("Performing rapid add/delete operations...");

        try {
            // Add several elements
            for (int i = 0; i < 5; i++) {
                addButton.click();
            }

            // Get all delete buttons
            java.util.List<WebElement> deleteButtons = driver.findElements(By.cssSelector(".added-manually"));

            ExtentReportManager.logInfo("Found " + deleteButtons.size() + " delete buttons");

            // ❌ PROBLEM: As we delete buttons, the list references become stale
            for (WebElement deleteBtn : deleteButtons) {
                deleteBtn.click(); // After first delete, other references may become stale
                Thread.sleep(50); // Small delay to let DOM update
            }

            ExtentReportManager.logPass("All buttons deleted successfully");

        } catch (StaleElementReferenceException e) {
            ExtentReportManager.logFail("StaleElementReferenceException during rapid operations!");
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Test(description = "✓ PASSES: But uses unreliable Thread.sleep - slow and fragile")
    public void testWithThreadSleep_SlowAndFragile() {
        // ❌ PROBLEM: This test passes but uses Thread.sleep which is:
        // - Too slow (always waits full duration even if element is ready)
        // - Too fragile (may fail on slower networks/machines)

        ExtentReportManager.logInfo("Demonstrating Thread.sleep anti-pattern");
        ExtentReportManager.logWarning("This test PASSES but is SLOW and FRAGILE");

        long startTime = System.currentTimeMillis();

        driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");

        try {
            // Click start
            driver.findElement(By.cssSelector("#start button")).click();

            ExtentReportManager.logInfo("Using Thread.sleep(6000) - wasteful and unreliable!");

            // ❌ BAD PRACTICE: Thread.sleep is an anti-pattern
            Thread.sleep(6000); // Always waits 6 seconds, even if element appears in 2 seconds

            WebElement finishElement = driver.findElement(By.cssSelector("#finish h4"));
            String text = finishElement.getText();

            long duration = System.currentTimeMillis() - startTime;

            Assert.assertTrue(text.contains("Hello World"));
            ExtentReportManager.logWarning("Test passed but took " + duration + "ms due to Thread.sleep");
            ExtentReportManager.logInfo("SeleniumCraft would complete this in ~2-3 seconds with smart waits");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
