package com.seleniumcraft.demo;

import com.seleniumcraft.core.SeleniumCraft;
import com.seleniumcraft.core.SmartElement;
import com.seleniumcraft.core.DriverContext;
import com.seleniumcraft.driver.DriverFactory;
import com.seleniumcraft.context.FrameHelper;
import com.seleniumcraft.wait.RetryEngine;
import com.seleniumcraft.reporting.ExtentReportManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.List;

/**
 * ✅ SELENIUMCRAFT TEST - THE NEW WAY ✅
 * 
 * This class demonstrates the benefits of using SeleniumCraft library:
 * - Clean, readable, fluent API
 * - Automatic wait handling (no Thread.sleep needed)
 * - Smart element auto-healing from StaleElementReferenceException
 * - Built-in frame context management
 * - Less boilerplate, more focus on test logic
 * - Resilient to timing and DOM changes
 */
@Listeners(com.seleniumcraft.reporting.ExtentTestListener.class)
public class SeleniumCraftTest {

    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        // ✅ SOLUTION: One-line browser initialization with DriverContext
        driver = DriverFactory.initChrome();
        driver.manage().window().maximize();
        driver.get("https://the-internet.herokuapp.com");
    }

    @AfterMethod
    public void tearDown() {
        // ✅ SOLUTION: Clean driver management
        DriverFactory.quit();
    }

    @Test
    public void testLoginFormWithSeleniumCraft() {
        // ✅ SOLUTION: waitAndClick() auto-waits until element is clickable - no
        // Thread.sleep needed
        // ✅ SOLUTION: SmartElement auto re-fetches if DOM refreshes - no
        // StaleElementException
        // ✅ SOLUTION: Fluent API makes intent clear in just 3 lines
        // ✅ SOLUTION: Automatic retry logic built into every element action

        System.out.println("✅ [SeleniumCraft] Login Test - Clean, fluent, resilient code");

        driver.get("https://the-internet.herokuapp.com/login");

        // ✅ SOLUTION: SeleniumCraft.$() factory + type() with built-in wait
        SeleniumCraft.$(By.id("username")).type("tomsmith");
        SeleniumCraft.$(By.id("password")).type("SuperSecretPassword!");

        // ✅ SOLUTION: waitAndClick() handles visibility + clickability automatically
        SeleniumCraft.$(By.cssSelector("button[type='submit']")).waitAndClick();

        // ✅ SOLUTION: RetryEngine waits until condition is met - no Thread.sleep
        RetryEngine.retryUntil(
                () -> SeleniumCraft.$(By.cssSelector(".flash.success")).getText()
                        .contains("You logged into a secure area"),
                50, // max retries
                200 // delay ms between retries
        );

        String successMessage = SeleniumCraft.$(By.cssSelector(".flash.success")).getText();
        Assert.assertTrue(successMessage.contains("You logged into a secure area"));
        System.out.println("✓ Login successful - SeleniumCraft handled all waits automatically");
    }

    @Test
    public void testCheckboxesWithSeleniumCraft() {
        // ✅ SOLUTION: SmartElement with auto-wait replaces verbose WebDriverWait
        // ✅ SOLUTION: waitAndClick() is self-documenting
        // ✅ SOLUTION: Automatic retry on element interactions

        System.out.println("✅ [SeleniumCraft] Checkbox Test - One-liner element operations");

        driver.get("https://the-internet.herokuapp.com/checkboxes");

        // ✅ SOLUTION: Use DriverContext.getDriver() for raw operations when needed
        List<WebElement> checkboxes = DriverContext.getDriver()
                .findElements(By.cssSelector("input[type='checkbox']"));

        Assert.assertEquals(checkboxes.size(), 2, "Should have 2 checkboxes");

        // ✅ SOLUTION: SmartElement for resilient click operations
        SeleniumCraft.css("input[type='checkbox']:first-child").waitAndClick();
        SeleniumCraft.css("input[type='checkbox']:last-child").waitAndClick();

        // Verify state using raw Selenium (isSelected not in SmartElement)
        checkboxes = DriverContext.getDriver()
                .findElements(By.cssSelector("input[type='checkbox']"));
        Assert.assertTrue(checkboxes.get(0).isSelected(), "First checkbox should be checked");
        Assert.assertFalse(checkboxes.get(1).isSelected(), "Second checkbox should be unchecked");

        System.out.println("✓ Checkbox test passed - Clean, maintainable code");
    }

    @Test
    public void testDropdownWithSeleniumCraft() {
        // ✅ SOLUTION: SmartElement handles waiting automatically
        // ✅ SOLUTION: Combine SmartElement waits with Select when needed
        // ✅ SOLUTION: Clean, readable code with built-in resilience

        System.out.println("✅ [SeleniumCraft] Dropdown Test - Intuitive select operations");

        driver.get("https://the-internet.herokuapp.com/dropdown");

        // ✅ SOLUTION: Wait for dropdown to be visible, then use Select
        SeleniumCraft.$(By.id("dropdown")).waitVisible();
        Select dropdown = new Select(DriverContext.getDriver().findElement(By.id("dropdown")));
        dropdown.selectByVisibleText("Option 1");

        // ✅ SOLUTION: Verify selection
        String selectedValue = dropdown.getFirstSelectedOption().getText();
        Assert.assertEquals(selectedValue, "Option 1", "Option 1 should be selected");

        System.out.println("✓ Dropdown selection completed - Fluent, readable API");
    }

    @Test
    public void testIframeWithSeleniumCraft() {
        // ✅ SOLUTION: FrameHelper.inside() always switches back - even if test throws
        // exception
        // ✅ SOLUTION: No risk of forgetting switchTo().defaultContent()
        // ✅ SOLUTION: Automatic context management with try-finally internally
        // ✅ SOLUTION: Nested iframe support with automatic context stacking

        System.out.println("✅ [SeleniumCraft] iFrame Test - Automatic context switching");

        driver.get("https://the-internet.herokuapp.com/iframe");

        // ✅ SOLUTION: Lambda-based context management - guaranteed to switch back
        // No driver parameter needed - FrameHelper uses DriverContext internally
        FrameHelper.inside("mce_0_ifr", () -> {
            // Code inside this lambda runs within iframe context
            // Context is automatically restored after lambda completes

            SmartElement contentArea = SeleniumCraft.$(By.id("tinymce"));
            String iframeText = contentArea.waitVisible().getText();

            Assert.assertFalse(iframeText.isEmpty(), "iFrame content should not be empty");
            System.out.println("✓ iFrame content accessed and verified");
        });

        // ✅ SOLUTION: Guaranteed that we're back in main context now
        System.out.println("✓ iFrame test passed - Context automatically restored");
    }

    @Test
    public void testDynamicLoadingWithSeleniumCraft() {
        // ✅ SOLUTION: RetryEngine with lambda is clean and powerful
        // ✅ SOLUTION: One line replaces 5 lines of verbose ExpectedConditions
        // ✅ SOLUTION: Easier to understand the wait condition at a glance

        System.out.println("✅ [SeleniumCraft] Dynamic Loading Test - Elegant retry logic");

        driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");

        // ✅ SOLUTION: Clean, one-liner for button click with auto-wait
        SeleniumCraft.css("button[type='button']").waitAndClick();

        // ✅ SOLUTION: RetryEngine elegantly handles wait for dynamic content
        RetryEngine.retryUntil(
                () -> {
                    try {
                        return SeleniumCraft.$(By.id("finish")).getText().contains("Hello World!");
                    } catch (Exception e) {
                        return false;
                    }
                },
                75, // max retries
                200 // delay ms (75 * 200 = 15s max)
        );

        String loadedText = SeleniumCraft.$(By.id("finish")).getText();
        Assert.assertFalse(loadedText.isEmpty(), "Loaded element should contain text");
        System.out.println("✓ Dynamic loading completed - "
                + "Clean, readable wait condition with automatic retry");
    }
}
