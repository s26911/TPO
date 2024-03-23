package zad1;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class Util {
    public static String getCountryISOCode(String country){
        Optional<String[]> loc = Arrays.stream(Locale.getISOCountries())
                .map(x -> new String[]{x, new Locale("ENG", x).getDisplayCountry()})
                .filter(x->x[1].equals(country))
                .findFirst();

        if(loc.isEmpty()){
            System.out.println("Incorrect country name!");
            return "";
        }

        return loc.get()[0];
    }
}
