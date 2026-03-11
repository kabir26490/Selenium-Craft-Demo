# 🚀 SeleniumCraft Demo

> **A side-by-side comparison of Raw Selenium vs SeleniumCraft library**

This project demonstrates the dramatic difference between traditional Selenium WebDriver code and the modern, resilient SeleniumCraft library approach.

---

## 📋 Overview

| Feature | Raw Selenium ❌ | SeleniumCraft ✅ |
|---------|----------------|------------------|
| Element waits | Manual `Thread.sleep()` or verbose `WebDriverWait` | Auto-wait built into every action |
| Stale elements | `StaleElementReferenceException` crashes | Auto-healing with retry logic |
| iFrame handling | Manual `switchTo().frame()` / `defaultContent()` | `FrameHelper.inside()` with auto-restore |
| Code verbosity | 5-10 lines per action | 1 line per action |
| Flaky tests | Common | Rare |

---

## 🏗️ Project Structure

```
seleniumcraft-demo/
├── pom.xml                          # Maven configuration
├── testng.xml                       # TestNG suite configuration
├── src/
│   ├── main/java/com/seleniumcraft/
│   │   ├── core/SeleniumCraft.java       # Fluent API ($, css, xpath)
│   │   ├── driver/DriverFactory.java     # Browser initialization
│   │   ├── element/SmartElement.java     # Auto-healing element wrapper
│   │   ├── wait/RetryEngine.java         # Lambda-based retry logic
│   │   ├── context/FrameHelper.java      # Safe iframe management
│   │   └── reporting/
│   │       ├── ExtentReportManager.java  # HTML report generation
│   │       └── ExtentTestListener.java   # TestNG listener
│   └── test/java/com/seleniumcraft/demo/
│       ├── BasicSeleniumTest.java        # Traditional Selenium (OLD WAY)
│       ├── SeleniumCraftTest.java        # SeleniumCraft (NEW WAY)
│       ├── FlakyBasicSeleniumTest.java   # Tests that FAIL with raw Selenium
│       └── ResilientSeleniumCraftTest.java # Same tests PASS with SeleniumCraft
└── test-output/
    └── extent-reports/               # HTML test reports with screenshots
```

---

## 🛠️ Technologies

- **Java 21**
- **Selenium WebDriver 4.18.1**
- **TestNG 7.9.0**
- **WebDriverManager 5.7.0**
- **ExtentReports 5.1.1**
- **Maven**

---

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.8+
- Chrome browser

### Build & Run

```bash
# Clone the repository
git clone https://github.com/kabir26490/Selenium-Craft-Demo.git
cd Selenium-Craft-Demo

# Build the project
mvn clean install -DskipTests

# Run all tests
mvn test

# Run only flaky tests (expected to fail)
mvn test -Dtest=FlakyBasicSeleniumTest

# Run only resilient tests (all pass)
mvn test -Dtest=ResilientSeleniumCraftTest

# Open test report (macOS)
open test-output/extent-reports/*.html
```

---

## 📊 Test Reports

After running tests, beautiful HTML reports are generated:

```
test-output/extent-reports/TestReport_YYYY-MM-DD_HH-mm-ss.html
```

Features:
- ✅ Pass/Fail status with details
- 📸 Automatic screenshots on failure
- 📈 Test categorization
- ⏱️ Execution time tracking

---

## 🔥 Code Comparison

### ❌ Traditional Selenium (Verbose, Fragile)

```java
// 10+ lines just to login
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
username.clear();
username.sendKeys("tomsmith");

WebElement password = driver.findElement(By.id("password"));
password.clear();
password.sendKeys("SuperSecretPassword!");

WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button")));
loginBtn.click();

Thread.sleep(2000); // ❌ Anti-pattern
```

### ✅ SeleniumCraft (Clean, Resilient)

```java
// 3 lines - same result, auto-waits, auto-retry
$("username").type("tomsmith");
$("password").type("SuperSecretPassword!");
css("button[type='submit']").waitAndClick();
```

---

## 🧪 Test Scenarios

### Flaky Tests (FlakyBasicSeleniumTest) - Expected to FAIL
| Test | Problem Demonstrated |
|------|---------------------|
| `testStaleElementException_WillFail` | StaleElementReferenceException |
| `testDynamicLoadingRaceCondition_WillFail` | Race condition with short wait |
| `testDisappearingElement_WillFail` | Transient element timing |
| `testRapidClicksStaleElement_WillFail` | Rapid DOM changes |

### Resilient Tests (ResilientSeleniumCraftTest) - All PASS
| Test | Solution Used |
|------|---------------|
| `testStaleElementAutoHealing_Passes` | SmartElement auto-retry |
| `testDynamicLoadingWithSmartRetry_Passes` | RetryEngine polling |
| `testTransientElementHandling_Passes` | Graceful wait handling |
| `testRapidInteractionsWithAutoRefresh_Passes` | Fresh element references |

---

## 📚 SeleniumCraft API

### Fluent Element Selectors
```java
import static com.seleniumcraft.core.SeleniumCraft.*;

$("id")                    // By ID
css(".class")              // By CSS selector
xpath("//button")          // By XPath
$$("input[type='checkbox']") // Multiple elements
```

### Smart Element Actions
```java
$("username").type("admin");           // Auto-wait + clear + type
css("button").waitAndClick();          // Wait visible + clickable + click
$("dropdown").waitAndSelect("Option"); // Select dropdown option
```

### iframe Handling
```java
FrameHelper.inside("iframe-id", () -> {
    $("element-inside").type("text");
    // Automatically switches back after lambda completes
});
```

### Retry Engine
```java
String result = RetryEngine.retryUntil(
    () -> $("element").getText(),
    text -> text.contains("expected"),
    10000  // timeout ms
);
```

---

## 👨‍💻 Author

**Kabir**
- GitHub: [@kabir26490](https://github.com/kabir26490)

---

## 📄 License

This project is licensed under the MIT License.

---

## ⭐ Star this repo if you found it helpful!
