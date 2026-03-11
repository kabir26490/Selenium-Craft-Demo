package com.seleniumcraft.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;

/**
 * DriverFactory - Simplified browser driver initialization and management.
 * Uses WebDriverManager for automatic driver binary management.
 */
public class DriverFactory {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    /**
     * Initializes Chrome browser with default settings.
     */
    public static WebDriver initChrome() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-infobars");
        WebDriver driver = new ChromeDriver(options);
        driverThreadLocal.set(driver);
        return driver;
    }

    /**
     * Initializes Chrome browser with custom arguments.
     */
    public static WebDriver initChrome(String... args) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments(args);
        WebDriver driver = new ChromeDriver(options);
        driverThreadLocal.set(driver);
        return driver;
    }

    /**
     * Initializes Chrome browser in headless mode.
     */
    public static WebDriver initChromeHeadless() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        WebDriver driver = new ChromeDriver(options);
        driverThreadLocal.set(driver);
        return driver;
    }

    /**
     * Initializes Firefox browser.
     */
    public static WebDriver initFirefox() {
        WebDriverManager.firefoxdriver().setup();
        WebDriver driver = new FirefoxDriver();
        driverThreadLocal.set(driver);
        return driver;
    }

    /**
     * Initializes Edge browser.
     */
    public static WebDriver initEdge() {
        WebDriverManager.edgedriver().setup();
        WebDriver driver = new EdgeDriver();
        driverThreadLocal.set(driver);
        return driver;
    }

    /**
     * Gets the current thread's WebDriver instance.
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Quits the current thread's WebDriver and clears it.
     */
    public static void quit() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
        }
    }
}
