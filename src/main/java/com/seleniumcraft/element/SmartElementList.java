package com.seleniumcraft.element;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * SmartElementList - A wrapper for List<SmartElement> that provides chainable methods.
 * Enables fluent API for multiple element operations.
 */
public class SmartElementList extends ArrayList<SmartElement> {

    private final WebDriver driver;
    private final By locator;
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    public SmartElementList(WebDriver driver, By locator) {
        this.driver = driver;
        this.locator = locator;
        refresh();
    }

    /**
     * Refreshes the element list from DOM.
     */
    private void refresh() {
        this.clear();
        List<WebElement> elements = driver.findElements(locator);
        for (int i = 0; i < elements.size(); i++) {
            this.add(new IndexedSmartElement(driver, locator, i));
        }
    }

    /**
     * Waits for all elements to be visible and returns this list.
     */
    public SmartElementList waitVisible() {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        refresh();
        return this;
    }

    /**
     * Waits for at least one element to be present and returns this list.
     */
    public SmartElementList waitPresent() {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        refresh();
        return this;
    }

    /**
     * Internal class for indexed element access - directly extends SmartElement.
     */
    private static class IndexedSmartElement extends SmartElement {
        private final WebDriver driver;
        private final By locator;
        private final int index;

        IndexedSmartElement(WebDriver driver, By locator, int index) {
            super(driver, locator);
            this.driver = driver;
            this.locator = locator;
            this.index = index;
        }

        @Override
        protected WebElement getElement() {
            List<WebElement> elements = driver.findElements(locator);
            if (index < elements.size()) {
                return elements.get(index);
            }
            throw new NoSuchElementException("Element at index " + index + " not found");
        }
    }
}
