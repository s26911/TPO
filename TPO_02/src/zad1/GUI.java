package zad1;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Currency;

class MyJLabel extends JLabel {
    public MyJLabel(String title) {
        super();
        setHorizontalAlignment(JLabel.CENTER);
        setBorder(new TitledBorder(title));
        setFont(new Font("Futura", Font.BOLD, 16));
    }
}

public class GUI extends JFrame {
    Service service;
    MyJLabel weather, currency, plnRate;
    JButton inputInfoButton;
    JFXPanel jfxPanel;

    public GUI(Service service) {
        this.service = service;
        setTitle("Webclients");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        weather = new MyJLabel("Weather");
        currency = new MyJLabel("Currency");
        plnRate = new MyJLabel("PLN Rate");
        inputInfoButton = new JButton("Change attributes");

        // information components
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 0.3;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 0.5;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(10, 10, 10, 10);
        inputInfoButton.setFont(new Font("Futura", Font.BOLD, 16));
        inputInfoButton.setBackground(new Color(199, 241, 255));
        inputInfoButton.addActionListener((x) -> {
            updateUI(userInputPopup());

        });
        add(inputInfoButton, constraints);

        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weighty = 1;
        add(weather, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weighty = 1;
        add(currency, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.weighty = 1;
        add(plnRate, constraints);

        // browser
        jfxPanel = new JFXPanel();
        Platform.runLater(() -> {
            WebView browser = new WebView();
            WebEngine webEngine = browser.getEngine();
            String www = "https://en.wikipedia.org/wiki/Warsaw";
            webEngine.load(www);

            Scene scene = new Scene(browser);
            jfxPanel.setScene(scene);
        });
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridheight = 4;
        constraints.weightx = 1;
        constraints.weighty = 1;
        add(jfxPanel, constraints);

        setVisible(true);
        String[] dataFromUserInput = userInputPopup();
        updateUI(dataFromUserInput);
    }

    // updates JLabels and browser site to match provided data
    public void updateUI(String[] data) {
        if (data.length != 6)
            return;

        String weatherInfo = Util.processWeatherJson(data[3]);
        weather.setText(weatherInfo);
        currency.setText("<html>" + Currency.getInstance(Util.getLocaleForCountry(service.getCountry())).getCurrencyCode()
                + " to " + data[2] + "<br/>" + data[4] + "<html/>");
        plnRate.setText("<html>" + "PLN to "
                + Currency.getInstance(Util.getLocaleForCountry(service.getCountry())).getCurrencyCode() + "<br/>" + data[5] + "<html/>");

        Platform.runLater(() -> {
            WebView browser = new WebView();
            WebEngine webEngine = browser.getEngine();
            String www = "https://en.wikipedia.org/wiki/" + data[1].substring(0, 1).toUpperCase() + data[1].substring(1);
            webEngine.load(www);

            Scene scene = new Scene(browser);
            jfxPanel.setScene(scene);
        });
    }


    // returns data input by the user in dialog window along with weather, rate and NBP rate for this data
    public String[] userInputPopup() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JTextField country = new JTextField();
        JTextField city = new JTextField();
        JTextField currencyCode = new JTextField();

        panel.add(new JLabel("Country: "));
        panel.add(country);
        panel.add(new JLabel("City: "));
        panel.add(city);
        panel.add(new JLabel("Currency code: "));
        panel.add(currencyCode);
        int yesNo = JOptionPane.showConfirmDialog(this, panel, "Please input information", JOptionPane.YES_NO_OPTION);
        if (yesNo == 1)
            return new String[1];

        try {
            service = new Service(country.getText());
            return new String[]{
                    country.getText().toLowerCase(),
                    city.getText(),
                    currencyCode.getText(),
                    service.getWeather(city.getText()),
                    service.getRateFor(currencyCode.getText()).toString(),
                    service.getNBPRate().toString()};
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return userInputPopup();
        }
    }
}
