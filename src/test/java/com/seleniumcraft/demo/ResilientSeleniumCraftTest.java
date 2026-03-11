package test.java.com.seleniumcraft.demo;

import com.seleniumcraft.driver.DriverFactory;
import com.seleniumcraft.element.SmartElement;
import com.seleniumcraft.reporting.ExtentReportManager;
import com.seleniumcraft.wait.RetryEngine;
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
        SmartElement checkbox = SmartElement.of(driver, By.cssSelector("#checkbox input"));
        SmartElement removeButton = SmartElement.of(driver, By.cssSelector("#checkbox button"));
        SmartElement message = SmartElement.of(driver, By.id("message"));

        ExtentReportManager.logInfo("Created SmartElement references (lazy evaluation)");

        // Check initial state
        boolean initialState = checkbox.isDisplayed();
        ExtentReportManager.logInfo("Checkbox initially displayed: " + initialState);

        // Click remove button
        removeButton.waitAndClick();
        ExtentReportManager.logInfo("Clicked 'Remove' button - DOM modified");

        // Wait for message to appear
        String messageText = RetryEngine.retryUntil(
                () -> message.getText(),
                text -> text != null && text.contains("gone"),
                10000);

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
        SmartElement.of(driver, By.cssSelector("#start button")).waitAndClick();
        ExtentReportManager.logInfo("Clicked 'Start' button - content loading...");

        // ✅ SOLUTION: RetryEngine keeps trying until element appears and has expected
        // text
        String loadedText = RetryEngine.retryUntil(
                () -> {
                    try {
                        return SmartElement.of(driver, By.cssSelector("#finish h4")).getText();
                    } catch (Exception e) {
                        return null; // Element not yet present
                    }
                },
                text -> text != null && !text.isEmpty() && text.contains("Hello World"),
                15000, // timeout
                200 // poll interval - check every 200ms
        );

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
        SmartElement enableButton = SmartElement.of(driver, By.cssSelector("#input-example button"));
        enableButton.waitAndClick();

        ExtentReportManager.logInfo("Clicked 'Enable' button - waiting for loading to complete");

        // ✅ SOLUTION: Wait for the message that confirms action is complete
        String resultMessage = RetryEngine.retryUntil(
                () -> {
                    try {
                        return SmartElement.of(driver, By.id("message")).getText();
                    } catch (Exception e) {
                        return "";
                    }
                },
                msg -> msg != null && msg.contains("enabled"),
                10000);

        ExtentReportManager.logPass("✓ Operation completed: '" + resultMessage + "'");

        // Verify input is now enabled
        SmartElement textInput = SmartElement.of(driver, By.cssSelector("#input-example input"));
        textInput.waitVisible().type("SeleniumCraft handles this gracefully!");

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

        SmartElement addButton = SmartElement.of(driver, By.cssSelector("button[onclick='addElement()']"));

        ExtentReportManager.logInfo("Adding 5 elements...");

        // Add several elements
        for (int i = 0; i < 5; i++) {
            addButton.waitAndClick();
        }

        ExtentReportManager.logInfo("Verifying elements were added...");

        // ✅ SOLUTION: Re-query elements each time instead of storing stale list
        int count = RetryEngine.retryUntil(
                () -> driver.findElements(By.cssSelector(".added-manually")).size(),
                size -> size == 5,
                5000);

        Assert.assertEquals(count, 5, "Should have 5 delete buttons");
        ExtentReportManager.logInfo("Found " + count + " delete buttons");

        // Delete all elements one by one
        ExtentReportManager.logInfo("Deleting all elements...");

        for (int i = 0; i < 5; i++) {
            // ✅ SOLUTION: Always query fresh - get the FIRST available button each time
            SmartElement deleteButton = SmartElement.of(driver, By.cssSelector(".added-manually"));
            deleteButton.waitAndClick();
        }

        // Verify all deleted
        int remainingCount = driver.findElements(By.cssSelector(".added-manually")).size();
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
        SmartElement.of(driver, By.cssSelector("#start button")).waitAndClick();
        ExtentReportManager.logInfo("Started dynamic loading...");

        // ✅ SOLUTION: Smart wait - returns immediately when element appears
        String text = RetryEngine.retryUntil(
                () -> {
                    try {
                        return SmartElement.of(driver, By.cssSelector("#finish h4")).getText();
                    } catch (Exception e) {
                        return null;
                    }
                },
                t -> t != null && t.contains("Hello World"),
                15000,
                100 // Poll every 100ms for maximum efficiency
        );

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
        SmartElement.of(driver, By.cssSelector("#checkbox button")).waitAndClick();

        String removeMessage = RetryEngine.retryUntil(
                () -> SmartElement.of(driver, By.id("message")).getText(),
                msg -> msg != null && msg.contains("gone"),
                10000);
        ExtentReportManager.logPass("✓ Checkbox removed: " + removeMessage);

        // Step 2: Add checkbox back
        ExtentReportManager.logInfo("Step 2: Adding checkbox back...");
        SmartElement.of(driver, By.cssSelector("#checkbox button")).waitAndClick();

        String addMessage = RetryEngine.retryUntil(
                () -> SmartElement.of(driver, By.id("message")).getText(),
                msg -> msg != null && msg.contains("back"),
                10000);
        ExtentReportManager.logPass("✓ Checkbox restored: " + addMessage);

        // Step 3: Enable text input
        ExtentReportManager.logInfo("Step 3: Enabling text input...");
        SmartElement.of(driver, By.cssSelector("#input-example button")).waitAndClick();

        String enableMessage = RetryEngine.retryUntil(
                () -> SmartElement.of(driver, By.id("message")).getText(),
                msg -> msg != null && msg.contains("enabled"),
                10000);
        ExtentReportManager.logPass("✓ Input enabled: " + enableMessage);

        // Step 4: Type in the enabled input
        ExtentReportManager.logInfo("Step 4: Typing in enabled input...");
        SmartElement.of(driver, By.cssSelector("#input-example input"))
                .waitVisible()
                .type("SeleniumCraft is awesome!");
        ExtentReportManager.logPass("✓ Successfully typed in input");

        ExtentReportManager.logPass("✓ Complex 4-step workflow completed successfully!");
        ExtentReportManager.logPass("✓ All dynamic elements handled gracefully - zero failures!");

        Assert.assertTrue(enableMessage.contains("enabled"), "Input should be enabled");
    }
}
