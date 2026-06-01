import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class App {
    private static final String PASSWORD_GENERATOR_PAGE =
            "https://www.calculator.net/password-generator.html";

    public static void main(String[] args) {
        applyChromeDriverPath();

        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        try {
            runPasswordTask(driver);
            Task2.printClientIp(driver);
            Task3.printForecastAndSave(driver);
        } catch (Exception exception) {
            System.out.println("Application error");
            System.out.println(exception.getMessage());
        } finally {
            driver.quit();
        }
    }

    private static void runPasswordTask(WebDriver driver) {
        driver.get(PASSWORD_GENERATOR_PAGE);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement passwordBlock = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("resultid")));

        String generatedPassword = readPassword(passwordBlock);
        System.out.println("[Task 1] Generated password: " + generatedPassword);
    }

    private static String readPassword(WebElement passwordBlock) {
        String passwordText = passwordBlock.getText()
                .replace("Your new password:", "")
                .trim();

        if (!passwordText.isEmpty()) {
            return passwordText;
        }

        throw new IllegalStateException("Password text was not found on the generator page.");
    }

    private static void applyChromeDriverPath() {
        String propertyPath = System.getProperty("chrome.driver.path");
        if (propertyPath != null && !propertyPath.trim().isEmpty()) {
            System.setProperty("webdriver.chrome.driver", propertyPath.trim());
            return;
        }

        String environmentPath = System.getenv("CHROME_DRIVER_PATH");
        if (environmentPath != null && !environmentPath.trim().isEmpty()) {
            System.setProperty("webdriver.chrome.driver", environmentPath.trim());
        }
    }
}
