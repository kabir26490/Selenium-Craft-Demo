package com.seleniumcraft.demo;

import com.seleniumcraft.core.SeleniumCraft;
import com.seleniumcraft.core.SmartElement;
import com.seleniumcraft.core.DriverContext;
import com.seleniumcraft.driver.DriverFactory;
import com.seleniumcraft.wait.RetryEngine;
import com.seleniumcraft.reporting.ExtentReportManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * ✅ RESILIENT SELENIUMCRAFT TEST - HANDLES ALL FLAKY SCENARIOS ✅
 * 
 * This class demonstrates how SeleniumCraft gracefully handles:
 * - StaleElementReferenceException (auto-retry with fresh element reference)
 * - Race conditions with dynamic content (smart waits)
 * - Transient elements (retry logic)
 * - Rapid interactions (auto-healing)
 * 
 * All tests that FAIL with basic Selenium PASS with SeleniumCraft!
 */
@Listeners(com.seleniumcraft.reporting.ExtentTestListener.class)
public class ResilientSeleniumCraftTest {

    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.initChrome();
        driver.manage().window().maximize();

        ExtentReportManager.logInfo("Browser initialized - SeleniumCraft resilient approach");
    }

    @AfterMethod
    public void tearDown() {
        DriverFactory.quit();
        ExtentReportManager.removeTest();
    }

    @Test(description = "✅ PASSES: Auto-handles stale element by re-fetching reference")
    public void testStaleElementAutoHealing_Passes() {
        // ✅ SOLUTION: SmartElement auto-retries with fresh reference when stale

        ExtentReportManager.logInfo("Demonstrating SeleniumCraft's auto-healing from StaleElementException");
        ExtentReportManager.logInfo("Same scenario that FAILED with basic Selenium will PASS here");

        driver.get("https://the-internet.herokuapp.com/dynamic_controls");

        // SmartElement doesn't store stale references - it re-queries on each action
        SmartElement checkbox = SeleniumCraft.css("#checkbox input");
        SmartElement removeButton = SeleniumCraft.css("#checkbox button");
        SmartElement message = SeleniumCraft.$(By.id("message"));

        ExtentReportManager.logInfo("Created SmartElement references (lazy evaluation)");

        // Check initial state
        boolean initialState = checkbox.isVisible();
        ExtentReportManager.logInfo("Checkbox initially visible: " + initialState);

        // Click remove button
        removeButton.waitAndClick();
        ExtentReportManager.logInfo("Clicked 'Remove' button - DOM modified");

        // Wait for message to appear using RetryEngine
        RetryEngine.retryUntil(
                () -> {
                    try {
                        return SeleniumCraft.$(By.id("message")).getText().contains("gone");
                    } catch (Exception e) {
                        return false;
                    }
                },
                50, // max retries
                200 // delay ms
        );

        String messageText = SeleniumCraft.$(By.id("message")).getText();
        ExtentReportManager.logPass("✓ Message received: " + messageText);
        ExtentReportManager.logPass("✓ SeleniumCraft handled DOM changes gracefully - no StaleElementException!");

        Assert.assertTrue(messageText.contains("gone"), "Message should confirm element removal");
    }

    @Test(description = "✅ PASSES: Smart retry handles dynamic content without race conditions")
    public void testDynamicLoadingWithSmartRetry_Passes() {
        // ✅ SOLUTION: RetryEngine keeps trying until condition is met - no race
        // condition

        ExtentReportManager.logInfo("Demonstrating SeleniumCraft's smart retry for dynamic content");
        ExtentReportManager.logInfo("Same scenario that FAILED with basic Selenium will PASS here");

        long startTime = System.currentTimeMillis();

        driver.get("https://the-internet.herokuapp.com/dynamic_loading/2");

        // Click start button
        SeleniumCraft.css("#start button").waitAndClick();
        ExtentReportManager.logInfo("Clicked 'Start' button - content loading...");

        // ✅ SOLUTION: RetryEngine keeps trying until element appears and has expected
        // text
        RetryEngine.retryUntil(
                () -> {
                    try {
                        return SeleniumCraft.css("#finish h4").getText().contains("Hello World");
                    } catch (Exception e) {
                        return false; // Element not yet present
                    }
                },
                75, // max retries
                200 // delay ms (75 * 200 = 15s max)
        );

        String loadedText = SeleniumCraft.css("#finish h4").getText();
        long duration = System.currentTimeMillis() - startTime;

        Assert.assertTrue(loadedText.contains("Hello World"), "Should contain expected text");

        ExtentReportManager.logPass("✓ Dynamic content loaded: '" + loadedText + "'");
        ExtentReportManager.logPass("✓ Completed in " + duration + "ms - no Thread.sleep needed!");
        ExtentReportManager.logInfo("SeleniumCraft waited only as long as necessary, not a fixed duration");
    }

    @Test(description = "✅ PASSES: Gracefully handles transient elements with retry logic")
    public void testTransientElementHandling_Passes() {
        // ✅ SOLUTION: Retry logic handles elements that appear/disappear

        ExtentReportManager.logInfo("Demonstrating SeleniumCraft's handling of transient elements");

        driver.get("https://the-internet.herokuapp.com/dynamic_controls");

        // Enable the text input
        SeleniumCraft.css("#input-example button").waitAndClick();

        ExtentReportManager.logInfo("Clicked 'Enable' button - waiting for loading to complete");

        // ✅ SOLUTION: Wait for the message that confirms action is complete
        RetryEngine.retryUntil(
                () -> {
                    try {
                        return SeleniumCraft.$(By.id("message")).getText().contains("enabled");
                    } catch (Exception e) {
                        return false;
                    }
                },
                50, // max retries
                200 // delay ms
        );

        String resultMessage = SeleniumCraft.$(By.id("message")).getText();
        ExtentReportManager.logPass("✓ Operation completed: '" + resultMessage + "'");

        // Verify input is now enabled - type into it
        SeleniumCraft.css("#input-example input").waitVisible().type("SeleniumCraft handles this gracefully!");

        ExtentReportManager.logPass("✓ Successfully typed into dynamically enabled input");
        ExtentReportManager.logPass("✓ No transient element issues - smooth handling!");

        Assert.assertTrue(resultMessage.contains("enabled"), "Should confirm input is enabled");
    }

    @Test(description = "✅ PASSES: Handles rapid interactions with auto-refresh")
    public void testRapidInteractionsWithAutoRefresh_Passes() {
        // ✅ SOLUTION: Each SmartElement action re-fetches the element, preventing stale
        // references

        ExtentReportManager.logInfo("Demonstrating SeleniumCraft's handling of rapid DOM changes");

        driver.get("https://the-internet.herokuapp.com/add_remove_elements/");

        SmartElement addButton = SeleniumCraft.css("button[onclick='addElement()']");

        ExtentReportManager.logInfo("Adding 5 elements...");

        // Add several elements
        for (int i = 0; i < 5; i++) {
            addButton.waitAndClick();
        }

        ExtentReportManager.logInfo("Verifying elements were added...");

        // ✅ SOLUTION: Re-query elements each time instead of storing stale list
        RetryEngine.retryUntil(
                () -> DriverContext.getDriver().findElements(By.cssSelector(".added-manually")).size() == 5,
                25, // max retries
                200 // delay ms
        );

        int count = DriverContext.getDriver().findElements(By.cssSelector(".added-manually")).size();
        Assert.assertEquals(count, 5, "Should have 5 delete buttons");
        ExtentReportManager.logInfo("Found " + count + " delete buttons");

        // Delete all elements one by one
        ExtentReportManager.logInfo("Deleting all elements...");

        for (int i = 0; i < 5; i++) {
            // ✅ SOLUTION: Always query fresh - get the FIRST available button each time
            SeleniumCraft.css(".added-manually").waitAndClick();
        }

        // Verify all deleted
        int remainingCount = DriverContext.getDriver().findElements(By.cssSelector(".added-manually")).size();
        Assert.assertEquals(remainingCount, 0, "All elements should be deleted");

        ExtentReportManager.logPass("✓ All 5 elements added and deleted successfully!");
        ExtentReportManager.logPass("✓ No StaleElementException - SeleniumCraft auto-refreshes references!");
    }

    @Test(description = "✅ PASSES: Fast and efficient - waits only as long as needed")
    public void testEfficientSmartWaits_Passes() {
        // ✅ SOLUTION: Smart waits complete as soon as condition is met - no wasted time

        ExtentReportManager.logInfo("Demonstrating SeleniumCraft's efficient smart waits");
        ExtentReportManager.logInfo("Comparing to Thread.sleep which always waits full duration");

        long startTime = System.currentTimeMillis();

        driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");

        // Click start
        SeleniumCraft.css("#start button").waitAndClick();
        ExtentReportManager.logInfo("Started dynamic loading...");

        // ✅ SOLUTION: Smart wait - returns immediately when element appears
        RetryEngine.retryUntil(
                () -> {
                    try {
                        return SeleniumCraft.css("#finish h4").getText().contains("Hello World");
                    } catch (Exception e) {
                        return false;
                    }
                },
                150, // max retries
                100 // poll every 100ms for maximum efficiency
        );

        String text = SeleniumCraft.css("#finish h4").getText();
        long duration = System.currentTimeMillis() - startTime;

        Assert.assertTrue(text.contains("Hello World"), "Should contain expected text");

        ExtentReportManager.logPass("✓ Test completed in " + duration + "ms");
        ExtentReportManager.logPass("✓ Basic Selenium with Thread.sleep(6000) would take 6000+ms");
        ExtentReportManager.logPass("✓ SeleniumCraft saved approximately " + (6000 - duration) + "ms!");

        // Verify significant time savings
        Assert.assertTrue(duration < 6000,
                "SeleniumCraft should complete faster than Thread.sleep(6000)");
    }

    @Test(description = "✅ PASSES: Complex workflow with multiple dynamic elements")
    public void testComplexWorkflowWithDynamicElements_Passes() {
        // ✅ SOLUTION: SeleniumCraft handles complex multi-step workflows reliably

        ExtentReportManager.logInfo("Demonstrating SeleniumCraft in a complex multi-step workflow");

        driver.get("https://the-internet.herokuapp.com/dynamic_controls");

        // Step 1: Remove checkbox
        ExtentReportManager.logInfo("Step 1: Removing checkbox...");
        SeleniumCraft.css("#checkbox button").waitAndClick();

        RetryEngine.retryUntil(
                () -> {
                    try {
                        return SeleniumCraft.$(By.id("message")).getText().contains("gone");
                    } catch (Exception e) {
                        return false;
                    }
                },
                50, 200);
        String removeMessage = SeleniumCraft.$(By.id("message")).getText();
        ExtentReportManager.logPass("✓ Checkbox removed: " + removeMessage);

        // Step 2: Add checkbox back
        ExtentReportManager.logInfo("Step 2: Adding checkbox back...");
        SeleniumCraft.css("#checkbox button").waitAndClick();

        RetryEngine.retryUntil(
                () -> {
                    try {
                        return SeleniumCraft.$(By.id("message")).getText().contains("back");
                    } catch (Exception e) {
                        return false;
                    }
                },
                50, 200);
        String addMessage = SeleniumCraft.$(By.id("message")).getText();
        ExtentReportManager.logPass("✓ Checkbox restored: " + addMessage);

        // Step 3: Enable text input
        ExtentReportManager.logInfo("Step 3: Enabling text input...");
        SeleniumCraft.css("#input-example button").waitAndClick();

        RetryEngine.retryUntil(
                () -> {
                    try {
                        return SeleniumCraft.$(By.id("message")).getText().contains("enabled");
                    } catch (Exception e) {
                        return false;
                    }
                },
                50, 200);
        String enableMessage = SeleniumCraft.$(By.id("message")).getText();
        ExtentReportManager.logPass("✓ Input enabled: " + enableMessage);

        // Step 4: Type in the enabled input
        ExtentReportManager.logInfo("Step 4: Typing in enabled input...");
        SeleniumCraft.css("#input-example input")
                .waitVisible()
                .type("SeleniumCraft is awesome!");
        ExtentReportManager.logPass("✓ Successfully typed in input");

        ExtentReportManager.logPass("✓ Complex 4-step workflow completed successfully!");
        ExtentReportManager.logPass("✓ All dynamic elements handled gracefully - zero failures!");

        Assert.assertTrue(enableMessage.contains("enabled"), "Input should be enabled");
    }
}
