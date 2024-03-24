package zad1;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;

public class GUI extends JFrame {
    Service service;

    public GUI(Service service) {
        this.service = service;
        setTitle("Webclients");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JFXPanel jfxPanel = new JFXPanel();
        add(jfxPanel);
        Platform.runLater(() -> {
            WebView browser = new WebView();
            WebEngine webEngine = browser.getEngine();
            String www = "https://en.wikipedia.org/wiki/Warsaw";
            webEngine.load(www);

            Scene scene = new Scene(browser);
            jfxPanel.setScene(scene);
        });

        setVisible(true);
        String[] dataForUserInput = userInputPopup();
    }

    public void updateUI(String[] data) {

    }


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
            Service newService = new Service(country.getText());
            return new String[]{newService.getWeather(city.getText()),
                    newService.getRateFor(currencyCode.getText()).toString(),
                    newService.getNBPRate().toString()};
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return userInputPopup();
        }
    }
}
