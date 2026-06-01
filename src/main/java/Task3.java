import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.WebDriver;

public class Task3 {
    private static final String FORECAST_ENDPOINT =
            "https://api.open-meteo.com/v1/forecast?latitude=56&longitude=44"
            + "&hourly=temperature_2m,rain"
            + "&current=cloud_cover"
            + "&timezone=Europe%2FMoscow"
            + "&forecast_days=1"
            + "&wind_speed_unit=ms";

    public static void printForecastAndSave(WebDriver driver) throws Exception {
        driver.get(FORECAST_ENDPOINT);

        JSONObject forecast = loadForecast(driver);
        String report = buildForecastReport(forecast);

        System.out.println("[Task 3] Daily forecast for Nizhny Novgorod");
        System.out.println(report);
        writeReport(report);
    }

    private static JSONObject loadForecast(WebDriver driver) throws Exception {
        String rawJson = Task2.extractResponseText(driver);
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(rawJson);
    }

    private static String buildForecastReport(JSONObject forecast) {
        JSONObject hourly = (JSONObject) forecast.get("hourly");
        JSONArray timeValues = (JSONArray) hourly.get("time");
        JSONArray temperatureValues = (JSONArray) hourly.get("temperature_2m");
        JSONArray rainValues = (JSONArray) hourly.get("rain");

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%-4s %-19s %-16s %-12s%n",
                "No.", "Date/time", "Temperature", "Rain (mm)"));
        builder.append("--------------------------------------------------------------")
                .append(System.lineSeparator());

        for (int index = 0; index < timeValues.size(); index++) {
            builder.append(formatRow(
                    index + 1,
                    timeValues.get(index).toString().replace('T', ' '),
                    temperatureValues.get(index),
                    rainValues.get(index)));
        }

        return builder.toString();
    }

    private static String formatRow(int rowNumber, String dateTime, Object temperature, Object rain) {
        return String.format(
                Locale.US,
                "%-4d %-19s %-16s %-12s%n",
                rowNumber,
                dateTime,
                formatValue(temperature) + " C",
                formatValue(rain));
    }

    private static String formatValue(Object value) {
        if (value instanceof Number) {
            return String.format(Locale.US, "%.2f", ((Number) value).doubleValue());
        }

        return String.valueOf(value);
    }

    private static void writeReport(String report) throws IOException {
        Path output = Paths.get("result", "forecast.txt");
        Files.createDirectories(output.getParent());
        Files.write(output, report.getBytes(StandardCharsets.UTF_8));
    }
}
