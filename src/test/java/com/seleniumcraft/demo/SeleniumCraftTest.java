package test.java.com.seleniumcraft.demo;

import com.seleniumcraft.driver.DriverFactory;
import com.seleniumcraft.wait.RetryEngine;
import com.seleniumcraft.context.FrameHelper;
import com.seleniumcraft.element.SmartElement;
import com.seleniumcraft.reporting.ExtentReportManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.*;

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
        // ✅ SOLUTION: One-line browser initialization
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

        // ✅ SOLUTION: One-liner type() method with built-in wait
        SmartElement.of(driver, By.id("username")).type("tomsmith");
        SmartElement.of(driver, By.id("password")).type("SuperSecretPassword!");

        // ✅ SOLUTION: waitAndClick() handles visibility + clickability automatically
        SmartElement.of(driver, By.cssSelector("button[type='submit']")).waitAndClick();

        // ✅ SOLUTION: RetryEngine with lambda for flexible wait conditions
        String successMessage = RetryEngine.retryUntil(
                () -> SmartElement.of(driver, By.cssSelector(".flash.success")).getText(),
                text -> text.contains("You logged into a secure area"),
                10000 // timeout in ms
        );

        Assert.assertTrue(successMessage.contains("You logged into a secure area"));
        System.out.println("✓ Login successful - SeleniumCraft handled all waits automatically");
    }

    @Test
    public void testCheckboxesWithSeleniumCraft() {
        // ✅ SOLUTION: One line replaces 5 lines of WebDriverWait boilerplate
        // ✅ SOLUTION: waitVisible().waitAndClick() is self-documenting
        // ✅ SOLUTION: Element list retrieval with automatic wait handling

        System.out.println("✅ [SeleniumCraft] Checkbox Test - One-liner element operations");

        driver.get("https://the-internet.herokuapp.com/checkboxes");

        // ✅ SOLUTION: Fluent, chainable API with built-in waits
        var checkboxes = SmartElement.ofMultiple(driver, By.cssSelector("input[type='checkbox']"))
                .waitVisible();

        Assert.assertEquals(checkboxes.size(), 2, "Should have 2 checkboxes");

        // ✅ SOLUTION: Clean, one-line operations for each action
        checkboxes.get(0).waitAndClick();
        checkboxes.get(1).waitAndClick();

        // ✅ SOLUTION: SmartElement automatically re-fetches if element becomes stale
        Assert.assertTrue(checkboxes.get(0).isSelected(), "First checkbox should be checked");
        Assert.assertFalse(checkboxes.get(1).isSelected(), "Second checkbox should be unchecked");

        System.out.println("✓ Checkbox test passed - Clean, maintainable code");
    }

    @Test
    public void testDropdownWithSeleniumCraft() {
        // ✅ SOLUTION: No need for separate Select class wrapper
        // ✅ SOLUTION: selectByText() is more intuitive than Select syntax
        // ✅ SOLUTION: Automatic wait handling built into every method

        System.out.println("✅ [SeleniumCraft] Dropdown Test - Intuitive select operations");

        driver.get("https://the-internet.herokuapp.com/dropdown");

        // ✅ SOLUTION: One-liner with built-in wait and select logic
        SmartElement.of(driver, By.id("dropdown"))
                .waitAndSelect("Option 1");

        // ✅ SOLUTION: Getting selected value is simple and readable
        String selectedValue = SmartElement.of(driver, By.id("dropdown"))
                .getSelectedText();

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
        FrameHelper.inside(driver, "mce_0_ifr", () -> {
            // Code inside this lambda runs within iframe context
            // Context is automatically restored after lambda completes

            SmartElement contentArea = SmartElement.of(driver, By.id("tinymce"));
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
        SmartElement.of(driver, By.cssSelector("button[type='button']"))
                .waitAndClick();

        // ✅ SOLUTION: RetryEngine elegantly handles wait for dynamic content
        String loadedText = RetryEngine.retryUntil(
                () -> SmartElement.of(driver, By.id("finish")).getText(),
                text -> !text.isEmpty() && text.contains("Hello World!"),
                15000 // timeout in ms
        );

        Assert.assertFalse(loadedText.isEmpty(), "Loaded element should contain text");
        System.out.println("✓ Dynamic loading completed - "
                + "Clean, readable wait condition with automatic retry");
    }
}
