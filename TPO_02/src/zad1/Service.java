/**
 * @author Gołębiewski Jakub S26911
 */

package zad1;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Currency;

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

    String getWeather(String city) {
        String countryISOCode = Util.getCountryISOCode(country);
        URL url = null;
        try {
            url = new URL(WEATHER_URL.replace("{city name}", city)
                    .replace("{country name}", country)
                    .replace("{API key}", WEATHER_API_KEY));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return Util.readJsonFromURL(url);
    }

    public Double getRateFor(String currencyCode) {
        Currency curr1 = Currency.getInstance(Util.getLocaleForCountry(country));
        Currency curr2;
        try {
            curr2 = Currency.getInstance(currencyCode.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency code!");
        }

        JsonObject jsonObject = null;
        try {
            URL url = new URL(CURRENCY_URL.replace("{API key}", CURRENCY_API_KEY)
                    .replace("{currency code}", curr1.getCurrencyCode()));
            String jsonData = Util.readJsonFromURL(url);

            jsonObject = (JsonObject) Jsoner.deserialize(jsonData);
        } catch (MalformedURLException | JsonException e) {
            throw new RuntimeException(e);
        }

        BigDecimal decimal = (BigDecimal) ((JsonObject) jsonObject.get("conversion_rates")).get(curr2.getCurrencyCode());
        return decimal.doubleValue();
    }

    Double getNBPRate() {
        Currency curr = Currency.getInstance(Util.getLocaleForCountry(country));
        if (curr.getCurrencyCode().equals("PLN"))
            return 1.;

        for (String table : new String[]{"A", "B"}) {
            JsonArray rates = null;
            try {
                URL url = new URL(NBP_URL.replace("{table}", table));
                String jsonData = Util.readJsonFromURL(url);

                JsonObject jsonObject = (JsonObject) ((JsonArray) Jsoner.deserialize(jsonData)).get(0);
                rates = (JsonArray) jsonObject.get("rates");
            } catch (MalformedURLException | JsonException e) {
                throw new RuntimeException(e);
            }


            for (int i = 0; i < rates.size(); i++) {
                JsonObject j = (JsonObject) rates.get(i);
                if (j.get("code").toString().equals(curr.getCurrencyCode()))
                    return ((BigDecimal) j.get("mid")).doubleValue();
            }
        }

        return 0.;
    }

}  
