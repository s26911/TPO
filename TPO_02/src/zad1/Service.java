/**
 *
 *  @author Gołębiewski Jakub S26911
 *
 */

package zad1;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import com.github.cliftonlabs.json_simple.*;

public class Service {
    final String WEATHER_API_KEY = "d3375a635117507428fc11822613f794";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q={city name},{country code}&appid={API key}&units=metric";
    String country;

    public Service(String country) {
        this.country = country;
    }

    String getWeather(String city) throws IOException, JsonException {
        String countryISOCode = Util.getCountryISOCode(country);
        URL url = new URL(WEATHER_URL.replace("{city name}", city)
                                    .replace("{country name}", country)
                                    .replace("{API key}", WEATHER_API_KEY));
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String in;
        String jsonData = "";
        while((in = reader.readLine()) != null)
            jsonData += in;

        return jsonData;
    }
//    Double getRateFor(String currencyCode){}
//    Double getNBPRate(){}

}  
