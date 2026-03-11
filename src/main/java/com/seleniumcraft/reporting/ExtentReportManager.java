package com.seleniumcraft.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ExtentReportManager - HTML report generation with screenshot capture.
 * Industry-standard reporting for test automation frameworks.
 */
public class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();
    private static String reportPath;

    /**
     * Initializes the ExtentReports instance with configured reporter.
     */
    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            reportPath = "test-output/extent-reports/TestReport_" + timestamp + ".html";

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setDocumentTitle("SeleniumCraft Test Report");
            sparkReporter.config().setReportName("Selenium vs SeleniumCraft Comparison");
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Framework", "SeleniumCraft");
        }
        return extent;
    }

    /**
     * Creates a new test in the report.
     */
    public static ExtentTest createTest(String testName) {
        ExtentTest test = getInstance().createTest(testName);
        testThreadLocal.set(test);
        return test;
    }

    /**
     * Gets the current thread's test.
     */
    public static ExtentTest getTest() {
        return testThreadLocal.get();
    }

    /**
     * Logs info to the current test.
     */
    public static void logInfo(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.info(message);
        }
    }

    /**
     * Logs pass to the current test.
     */
    public static void logPass(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.pass(message);
        }
    }

    /**
     * Logs fail to the current test.
     */
    public static void logFail(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.fail(message);
        }
    }

    /**
     * Logs warning to the current test.
     */
    public static void logWarning(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.warning(message);
        }
    }

    /**
     * Removes the current test from thread local (for cleanup).
     */
    public static void removeTest() {
        testThreadLocal.remove();
    }

    /**
     * Captures a screenshot and saves it to the screenshots folder.
     *
     * @param driver         The WebDriver instance
     * @param screenshotName Name for the screenshot file
     * @return Path to the saved screenshot
     */
    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());
        String fileName = screenshotName + "_" + timestamp + ".png";
        String screenshotPath = "test-output/screenshots/" + fileName;

        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(screenshotPath);
            FileUtils.copyFile(srcFile, destFile);
            return screenshotPath;
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }

    /**
     * Logs a failure with screenshot attached.
     */
    public static void logFailWithScreenshot(WebDriver driver, String message, Throwable throwable) {
        ExtentTest test = getTest();
        if (test != null) {
            String screenshotPath = captureScreenshot(driver, "failure");
            if (screenshotPath != null) {
                try {
                    test.fail(message)
                            .fail(throwable)
                            .addScreenCaptureFromPath("../" + screenshotPath);
                } catch (Exception e) {
                    test.fail(message).fail(throwable);
                }
            } else {
                test.fail(message).fail(throwable);
            }
        }
    }

    /**
     * Flushes the report to disk.
     */
    public static void flush() {
        if (extent != null) {
            extent.flush();
            System.out.println("📊 Report generated: " + reportPath);
        }
    }

    /**
     * Gets the path to the generated report.
     */
    public static String getReportPath() {
        return reportPath;
    }
}
