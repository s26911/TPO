import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ClientGUI extends javax.swing.JFrame {

    String[] subscribed;
    String[] topics;
    Client client;

    public ClientGUI(Client client) throws HeadlessException, IOException {
        this.client = client;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Client Messenger");
        setSize(900, 600);

        subscribed = client.getSubscribed();
        topics = client.getTopics();
//        subscribed = new String[10];
//        topics = new String[10];

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

        JList<String> subscribedList = new JList<>(subscribed);
        constraints.weightx = .5;
        constraints.weighty = 0.5;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridheight = 3;
        constraints.gridwidth = 1;
        add(subscribedList, constraints);

        JList<String> topicsList = new JList<>(topics);
        constraints.gridx = 1;
        add(topicsList, constraints);

        JButton subscribeButton = new JButton("Subscribe");
        constraints.weightx = 0.5;
        constraints.weighty = 0.1;
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridheight = 1;
        add(subscribeButton, constraints);

        JButton unsubscribeButton = new JButton("Unsubscribe");
        constraints.gridx = 1;
        add(unsubscribeButton, constraints);

        JTextArea textArea = new JTextArea();
        textArea.setText("Hello");
        constraints.weighty = 2;
        constraints.weightx = 2;
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.gridheight = 4;
        add(textArea, constraints);

        textArea.setBackground(Color.CYAN);
        setVisible(true);
    }
}
