# SeleniumCraft: Kinetic Interaction Standard
> **Architectural Paradigm for 2026 Test Engineering.**

[![Documentation Status](https://img.shields.io/badge/docs-live-brightgreen)](https://kabir26490.github.io/Selenium-Craft-Demo/)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

**SeleniumCraft** is a high-precision architectural layer over Selenium 4. It treats web interactions as **Atomic Transactions**, eliminating the flakiness of traditional WebDriver scripts through a **Kinetic Execution Engine**.

---

## 🏛️ Architectural Intelligence
This repository is an empirical demonstration of technical superiority over "Raw Selenium."

### The Kinetic Engine vs. Standard WebDriver
Our engine moves beyond passive element polling. It implements a **Re-acquisition Lifecycle**:
1. **Contextual Awareness:** Active monitoring of the DOM during the interaction event loop.
2. **Atomic Healing:** Transparently resolving `StaleElementReferenceException` before it breaks the thread.
3. **Transactional Scoping:** Automated state recovery for nested iFrames and Shadow-DOM trees.

---

## 📂 Project Anatomy
```
Selenium-Craft-Demo/
├── index.html                  # [LIVE] Obsidian Architecture Root
├── docs/                       # Technical Assets & Infrastructure
│   ├── assets/                 # Architecture Blueprints & Evidence
│   │   ├── click-flow-architecture.svg
│   │   └── latest-report.html  # Standardized Verification Result
│   └── test-output/            # Archival Trace Reports
├── src/
│   ├── main/java/com/seleniumcraft/
│   │   └── reporting/          # Kinetic Observability (ExtentReports)
│   └── test/java/com/seleniumcraft/demo/
│       ├── SeleniumCraftTest.java         # [PASS] Kinetic Stability Proof
│       └── FlakyBasicSeleniumTest.java    # [FAIL] Legacy Instability Baseline
├── test-output/                # Local Dynamic Reports & Screenshots
├── pom.xml                     # dependency Management (Java 21 / Selenium 4)
└── testng.xml                  # Execution Orchestration
```

---

## 🚀 Live Documentation
The architectural narrative, including the **Kinetic Interaction Lifecycle** diagram and performance benchmarks, is hosted live:

👉 **[https://kabir26490.github.io/Selenium-Craft-Demo/](https://kabir26490.github.io/Selenium-Craft-Demo/)**

---

## 💻 Tech Stack
- **Runtime:** Java 21 (LTS)
- **Engine:** Selenium 4.18.1
- **Discovery:** WebDriverManager 5.7.0
- **Orchestration:** TestNG 7.9.0
- **Observability:** ExtentReports 5.1.1 (Metadata-rich artifacts)

---

## 🛠️ Local Execution
```bash
# Clone the repository
git clone https://github.com/kabir26490/Selenium-Craft-Demo.git
cd Selenium-Craft-Demo

# Execute Empirical Comparison
mvn clean test

# View Latest Artifact (macOS)
open docs/assets/latest-report.html
```

---

## 👤 Author
**Aakar Gupte**  
[GitHub Profile](https://github.com/kabir26490)
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
