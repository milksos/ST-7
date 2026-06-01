import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Task2 {
    private static final String IP_SERVICE_URL = "https://api.ipify.org/?format=json";

    public static void printClientIp(WebDriver driver) throws Exception {
        driver.get(IP_SERVICE_URL);

        String rawJson = extractResponseText(driver);
        String ipAddress = parseIpAddress(rawJson);

        System.out.println("[Task 2] Client IPv4: " + ipAddress);
    }

    public static String extractResponseText(WebDriver driver) {
        if (!driver.findElements(By.tagName("pre")).isEmpty()) {
            return driver.findElement(By.tagName("pre")).getText();
        }

        return driver.findElement(By.tagName("body")).getText();
    }

    private static String parseIpAddress(String rawJson) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject payload = (JSONObject) parser.parse(rawJson);
        Object ipField = payload.get("ip");

        if (ipField == null) {
            throw new IllegalStateException("The JSON response does not contain the ip field.");
        }

        return ipField.toString();
    }
}
