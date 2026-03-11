package com.seleniumcraft.context;

import org.openqa.selenium.WebDriver;

/**
 * FrameHelper - Safe iframe context management with automatic cleanup.
 * Guarantees that driver switches back to default content even if exceptions
 * occur.
 */
public class FrameHelper {

    /**
     * Executes an action inside a frame context, automatically switching back
     * afterwards.
     * Uses try-finally internally to guarantee context restoration.
     *
     * @param driver        The WebDriver instance
     * @param frameIdOrName The frame's id or name attribute
     * @param action        The action to perform inside the frame
     */
    public static void inside(WebDriver driver, String frameIdOrName, Runnable action) {
        try {
            driver.switchTo().frame(frameIdOrName);
            action.run();
        } finally {
            driver.switchTo().defaultContent();
        }
    }

    /**
     * Executes an action inside a frame context by index, automatically switching
     * back.
     *
     * @param driver     The WebDriver instance
     * @param frameIndex The zero-based frame index
     * @param action     The action to perform inside the frame
     */
    public static void insideByIndex(WebDriver driver, int frameIndex, Runnable action) {
        try {
            driver.switchTo().frame(frameIndex);
            action.run();
        } finally {
            driver.switchTo().defaultContent();
        }
    }

    /**
     * Executes an action inside nested frames, automatically switching back.
     *
     * @param driver     The WebDriver instance
     * @param frameNames Array of frame names/ids to navigate through (outermost
     *                   first)
     * @param action     The action to perform inside the innermost frame
     */
    public static void insideNested(WebDriver driver, String[] frameNames, Runnable action) {
        try {
            for (String frameName : frameNames) {
                driver.switchTo().frame(frameName);
            }
            action.run();
        } finally {
            driver.switchTo().defaultContent();
        }
    }
}
