import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class AdministratorGUI extends JFrame {
    Administrator admin;

    public static void main(String[] args) {
        new AdministratorGUI(new Administrator("localhost", 50000));
    }

    public AdministratorGUI(Administrator admin) {
        this.admin = admin;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Administrator panel");
        setSize(900, 600);
        setLayout(new FlowLayout());

        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(450, 500));
        JTextArea textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension(450, 500));
        textArea.setEditable(false);
        JPanel textPanel = new JPanel();
        textPanel.add(textField);
        textPanel.add(textArea);
        JPanel buttonsPanel = new JPanel();
        JButton addTopic = new JButton("Add topic");
        JButton delTopic = new JButton("Delete topic");
        JButton send = new JButton("Send text");
        buttonsPanel.add(addTopic);
        buttonsPanel.add(delTopic);
        buttonsPanel.add(send);

        addTopic.addActionListener(e -> {
            String topic = textField.getText().strip();
            try {
                admin.addTopic(topic);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        delTopic.addActionListener(e -> {
            String topic = textField.getText().strip();
            try {
                admin.delTopic(topic);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        send.addActionListener(e -> {
            String text = textField.getText().strip();
            String topic = text.substring(0, text.indexOf(" "));
            text = text.substring(text.indexOf(" ") + 1);
            try {
                admin.send(topic, text);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });



        add(textPanel);
        add(buttonsPanel);

        setVisible(true);
    }
}
