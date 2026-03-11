package com.seleniumcraft.reporting;

import com.seleniumcraft.core.DriverContext;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * ExtentTestListener - TestNG listener that integrates with ExtentReports.
 * Automatically creates tests, logs results, and captures screenshots on
 * failure.
 */
public class ExtentTestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  TEST SUITE STARTED: " + context.getName());
        System.out.println("═══════════════════════════════════════════════════════════════");
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        ExtentReportManager.createTest(className + " :: " + testName);
        ExtentReportManager.logInfo("Test started: " + testName);
        System.out.println("\n▶ Starting: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentReportManager.logPass("Test PASSED: " + testName);
        System.out.println("✅ PASSED: " + testName);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();

        // Use DriverContext to get the current thread's driver for screenshots
        WebDriver driver = null;
        try {
            driver = DriverContext.getDriver();
        } catch (Exception e) {
            // Driver may not be available (e.g., basic Selenium tests without
            // DriverContext)
        }

        if (driver != null) {
            ExtentReportManager.logFailWithScreenshot(driver, "Test FAILED: " + testName, throwable);
        } else {
            ExtentReportManager.logFail("Test FAILED: " + testName);
            if (throwable != null) {
                ExtentReportManager.getTest().fail(throwable);
            }
        }

        System.out.println("❌ FAILED: " + testName);
        if (throwable != null) {
            System.out.println("   Reason: " + throwable.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentReportManager.createTest(testName);
        ExtentReportManager.getTest().skip("Test SKIPPED: " + testName);
        System.out.println("⏭ SKIPPED: " + testName);
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.flush();
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("  TEST SUITE FINISHED: " + context.getName());
        System.out.println("  Passed: " + context.getPassedTests().size());
        System.out.println("  Failed: " + context.getFailedTests().size());
        System.out.println("  Skipped: " + context.getSkippedTests().size());
        System.out.println("═══════════════════════════════════════════════════════════════");
    }
}
