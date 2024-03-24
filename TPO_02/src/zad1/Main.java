/**
 * @author Gołębiewski Jakub S26911
 */

package zad1;

import com.github.cliftonlabs.json_simple.JsonException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, JsonException {
        Service s = new Service("Poland");
        String weatherJson = s.getWeather("Gdansk");
        Double rate1 = s.getRateFor("USD");
        Double rate2 = s.getNBPRate();
        // ...
        // część uruchamiająca GUI
        GUI gui = new GUI(s);
    }
}

