import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class App {
    private static final String PASSWORD_GENERATOR_URL =
            "https://www.calculator.net/password-generator.html";

    public static void main(String[] args) {
        configureChromeDriverPath();

        WebDriver webDriver = new ChromeDriver();
        try {
            runTask1(webDriver);
            Task2.printClientIp(webDriver);
            Task3.printForecastAndSave(webDriver);
        } catch (Exception e) {
            System.out.println("Error");
            System.out.println(e.toString());
        } finally {
            webDriver.quit();
        }
    }

    private static void runTask1(WebDriver webDriver) {
        webDriver.get(PASSWORD_GENERATOR_URL);

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        String password = extractGeneratedPassword(webDriver);
        System.out.println("Задание 1. Сгенерированный пароль: " + password);
    }

    private static String extractGeneratedPassword(WebDriver webDriver) {
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#resultid .verybigtext b")));

            String password = (String) ((JavascriptExecutor) webDriver).executeScript(
                    "var element = document.querySelector('#resultid .verybigtext b');" +
                    "return element ? element.textContent.trim() : null;");

            if (password != null && !password.isEmpty()) {
                return password;
            }
        } catch (TimeoutException e) {
            // If the page layout changes, fall back to a broader DOM query below.
        }

        String password = (String) ((JavascriptExecutor) webDriver).executeScript(
                "var selectors = ['#resultid .verybigtext b', '#resultid .verybigtext', '#resultid b'];" +
                "for (var i = 0; i < selectors.length; i++) {" +
                "  var element = document.querySelector(selectors[i]);" +
                "  if (element && element.textContent && element.textContent.trim()) {" +
                "    return element.textContent.trim();" +
                "  }" +
                "}" +
                "return null;");

        if (password != null && !password.isEmpty()) {
            return password;
        }

        throw new IllegalStateException("Password was not found on the page.");
    }

    private static void configureChromeDriverPath() {
        String systemPropertyPath = System.getProperty("chrome.driver.path");
        if (systemPropertyPath != null && !systemPropertyPath.trim().isEmpty()) {
            System.setProperty("webdriver.chrome.driver", systemPropertyPath.trim());
            return;
        }

        String envPath = System.getenv("CHROME_DRIVER_PATH");
        if (envPath != null && !envPath.trim().isEmpty()) {
            System.setProperty("webdriver.chrome.driver", envPath.trim());
        }
    }
}
