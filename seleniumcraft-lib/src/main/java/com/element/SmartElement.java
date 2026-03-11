package com.seleniumcraft.element;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SmartElement - Auto-healing element wrapper with built-in waits and retry
 * logic.
 * Automatically handles StaleElementReferenceException by re-fetching the
 * element.
 */
public class SmartElement {

    private final WebDriver driver;
    private final By locator;
    private WebElement element;
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    protected SmartElement(WebDriver driver, By locator) {
        this.driver = driver;
        this.locator = locator;
    }

    /**
     * Creates a SmartElement for a single element.
     */
    public static SmartElement of(WebDriver driver, By locator) {
        return new SmartElement(driver, locator);
    }

    /**
     * Creates a SmartElementList for multiple elements with chainable API.
     */
    public static SmartElementList ofMultiple(WebDriver driver, By locator) {
        return new SmartElementList(driver, locator);
    }

    /**
     * Gets the underlying WebElement, auto-healing if stale.
     */
    protected WebElement getElement() {
        try {
            if (element == null) {
                WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
                element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            }
            // Check if element is stale by trying to access it
            element.isEnabled();
            return element;
        } catch (StaleElementReferenceException e) {
            // Auto-heal: re-fetch the element
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
            element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return element;
        }
    }

    /**
     * Waits for element to be visible and returns this SmartElement.
     */
    public SmartElement waitVisible() {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return this;
    }

    /**
     * Waits for element to be clickable and clicks it.
     */
    public void waitAndClick() {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
                wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
                return;
            } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
                if (i == maxRetries - 1)
                    throw e;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    /**
     * Clears the element and types the given text.
     */
    public void type(String text) {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                WebElement el = getElement();
                el.clear();
                el.sendKeys(text);
                return;
            } catch (StaleElementReferenceException e) {
                element = null; // Force re-fetch
                if (i == maxRetries - 1)
                    throw e;
            }
        }
    }

    /**
     * Selects an option by visible text from a dropdown.
     */
    public void waitAndSelect(String visibleText) {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        WebElement selectElement = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        Select select = new Select(selectElement);
        select.selectByVisibleText(visibleText);
    }

    /**
     * Gets the selected option's text from a dropdown.
     */
    public String getSelectedText() {
        Select select = new Select(getElement());
        return select.getFirstSelectedOption().getText();
    }

    /**
     * Gets the text content of the element.
     */
    public String getText() {
        return getElement().getText();
    }

    /**
     * Checks if the element is selected (for checkboxes/radio buttons).
     */
    public boolean isSelected() {
        return getElement().isSelected();
    }

    /**
     * Checks if the element is displayed.
     */
    public boolean isDisplayed() {
        try {
            return getElement().isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Gets the size of elements matching the locator.
     */
    public int size() {
        return driver.findElements(locator).size();
    }
}
