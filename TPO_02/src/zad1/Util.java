package zad1;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class Util {
    // returns ISO code of a country name
    public static String getCountryISOCode(String country) throws IllegalArgumentException {
        Optional<String[]> loc = Arrays.stream(Locale.getISOCountries())
                .map(x -> new String[]{x, new Locale("ENG", x).getDisplayCountry()})
                .filter(x -> x[1].equalsIgnoreCase(country))
                .findFirst();

        if (loc.isEmpty()) {
            throw new IllegalArgumentException("Incorrect country name!");
        }

        return loc.get()[0];
    }

    // returns Locale for country name
    public static Locale getLocaleForCountry(String country) {
        Optional<Object[]> loc = Arrays.stream(Locale.getISOCountries())
                .map(x -> new Object[]{new Locale("ENG", x), new Locale("ENG", x).getDisplayCountry()})
                .filter(x -> ((String) x[1]).equalsIgnoreCase(country))
                .findFirst();

        if (loc.isEmpty()) {
            throw new IllegalArgumentException("Incorrect country name!");
        }

        return (Locale) loc.get()[0];
    }

    // returns raw json data from URL as String
    public static String readJsonFromURL(URL url) {
        BufferedReader reader = null;
        String jsonData = "";
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String in;
            while ((in = reader.readLine()) != null)
                jsonData += in;
            reader.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("Incorrect city name!");
        }

        return jsonData;
    }

    // returns processed json weather information ready to display on JLabel
    public static String processWeatherJson(String jsonData) {
        JsonObject jsonObject;
        try {
            jsonObject = (JsonObject) Jsoner.deserialize(jsonData);
        } catch (JsonException e) {
            throw new RuntimeException(e);
        }

        return "<html>Location: " + jsonObject.get("name").toString() + "<br/>" +
                "Sky: " + ((JsonObject) ((JsonArray) jsonObject.get("weather")).get(0)).get("main") + "<br/>" +
                "Temperature: " + ((JsonObject) jsonObject.get("main")).get("temp") + "<br/>" +
                "Pressure: " + ((JsonObject) jsonObject.get("main")).get("pressure") + "<br/>" +
                "Humidity: " + ((JsonObject) jsonObject.get("main")).get("humidity") + "<br/>" +
                "Wind: " + ((JsonObject) jsonObject.get("wind")).get("speed") + "</html>";
    }
}
