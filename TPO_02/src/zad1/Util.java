package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class Util {
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
}
