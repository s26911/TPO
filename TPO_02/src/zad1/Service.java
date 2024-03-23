/**
 * @author Gołębiewski Jakub S26911
 */

package zad1;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

import com.github.cliftonlabs.json_simple.*;

public class Service {
    final String WEATHER_API_KEY = "d3375a635117507428fc11822613f794";
    final String CURRENCY_API_KEY = "1a91277e8ddb6c1555c0c428";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q={city name},{country code}&appid={API key}&units=metric";
    final String CURRENCY_URL = "https://v6.exchangerate-api.com/v6/{API key}/latest/{currency code}";
    final String NBP_URL = "http://api.nbp.pl/api/exchangerates/tables/{table}/?format=json";
    String country;

    public Service(String country) {
        this.country = country;
    }

    String getWeather(String city) throws IOException, JsonException {
        String countryISOCode = Util.getCountryISOCode(country);
        URL url = new URL(WEATHER_URL.replace("{city name}", city)
                .replace("{country name}", country)
                .replace("{API key}", WEATHER_API_KEY));

        return Util.readJsonFromURL(url);
    }

    public Double getRateFor(String currencyCode) throws IOException, JsonException {
        Currency curr1 = Currency.getInstance(Util.getLocaleForCountry(country));
        Currency curr2 = Currency.getInstance(currencyCode);

        URL url = new URL(CURRENCY_URL.replace("{API key}", CURRENCY_API_KEY)
                .replace("{currency code}", curr1.getCurrencyCode()));
        String jsonData = Util.readJsonFromURL(url);

        JsonObject jsonObject = (JsonObject) Jsoner.deserialize(jsonData);
        BigDecimal decimal = (BigDecimal) ((JsonObject) jsonObject.get("conversion_rates")).get(curr2.getCurrencyCode());
        return decimal.doubleValue();

    }

    Double getNBPRate() throws IOException, JsonException {
        Currency curr = Currency.getInstance(Util.getLocaleForCountry(country));
        if (curr.getCurrencyCode().equals("PLN"))
            return 1.;

        {   // for table A
            URL url = new URL(NBP_URL.replace("{table}", "A"));
            String jsonData = Util.readJsonFromURL(url);

            JsonObject jsonObject = (JsonObject) ((JsonArray) Jsoner.deserialize(jsonData)).get(0);
            JsonArray rates = (JsonArray) jsonObject.get("rates");

            for (int i = 0; i < rates.size(); i++) {
                JsonObject j = (JsonObject) rates.get(i);
                if (j.get("code").toString().equals(curr.getCurrencyCode()))
                    return ((BigDecimal) ((JsonObject) j).get("mid")).doubleValue();
            }
        }
        {   // for table B
            URL url = new URL(NBP_URL.replace("{table}", "B"));
            String jsonData = Util.readJsonFromURL(url);

            JsonObject jsonObject = (JsonObject) ((JsonArray) Jsoner.deserialize(jsonData)).get(0);
            JsonArray rates = (JsonArray) jsonObject.get("rates");

            for (int i = 0; i < rates.size(); i++) {
                JsonObject j = (JsonObject) rates.get(i);
                if (j.get("code").toString().equals(curr.getCurrencyCode()))
                    return ((BigDecimal) ((JsonObject) j).get("mid")).doubleValue();
            }
        }

        return 0.;
    }

}  
