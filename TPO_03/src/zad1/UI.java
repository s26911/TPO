package zad1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UI extends JFrame {
    Client client;
    String selectedLang;

    public UI(Client client) throws HeadlessException {
        this.client = client;

        // General
        setTitle("Client UI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setSize(600, 400);
        setLocationRelativeTo(null);

        // ComboBox langs
        JComboBox<String> langs = new JComboBox<>();
        langs.addActionListener(e -> {
            selectedLang = langs.getSelectedItem() == null ||
                    langs.getSelectedItem().equals("Please select a language") ? null : langs.getSelectedItem().toString();
        });

        // JButton updateLanguages
        JButton updateLanguages = new JButton("Update Languages");
        updateLanguages.addActionListener(e -> {
            langs.removeAllItems();
            langs.addItem("Please select a language");
            for (String elem : client.askForLanguages())
                langs.addItem(elem);

            selectedLang = null;
        });
        updateLanguages.doClick();

        JTextArea textArea = new JTextArea();
        JTextArea translationsArea = new JTextArea();
        translationsArea.setEditable(false);
        translationsArea.setFocusable(false);
        JButton translate = new JButton("Translate");
        translate.addActionListener(e -> {
            if (selectedLang != null)
                translationsArea.setText(client.translate(textArea.getText().toLowerCase(), selectedLang));
        });


        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 0.3;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 0.02;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(10, 50, 10, 10);
        add(updateLanguages, constraints);

        constraints.insets = new Insets(10, 10, 10, 50);
        constraints.gridx = 1;
        add(langs, constraints);

        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.weighty = 0.7;
        constraints.gridx = 0;
        constraints.gridy = 1;
        add(textArea, constraints);

        constraints.gridx = 1;
        add(translationsArea, constraints);

        constraints.gridy = 2;
        constraints.gridx = 0;
        constraints.weighty = 0.01;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        add(translate, constraints);

        setVisible(true);
    }
}
