import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientGUI extends javax.swing.JFrame {

    String[] subscribed;
    String[] topics;
    Client client;

    public static void main(String[] args) throws IOException {
        new ClientGUI(new Client("localhost", 50000));
    }

    public ClientGUI(Client client) throws HeadlessException, IOException {
        this.client = client;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Client Messenger");
        setSize(900, 600);

        subscribed = client.getSubscribed();
        topics = client.getTopics();
//        subscribed = new String[10];
//        topics = new String[10];

        setLayout(new BorderLayout());

        JList<String> subscribedList = new JList<>(subscribed);
        subscribedList.setPreferredSize(new Dimension(200, 500));
        JList<String> topicsList = new JList<>(topics);
        topicsList.setPreferredSize(new Dimension(200, 500));
        JPanel topicsPanel = new JPanel();
        topicsPanel.add(subscribedList);
        topicsPanel.add(topicsList);

        JPanel buttons = new JPanel();
        JButton subscribeButton = new JButton("Subscribe");
        JButton unsubscribeButton = new JButton("Unsubscribe");
        JButton refresh = new JButton("Refresh");
        buttons.add(subscribeButton);
        buttons.add(unsubscribeButton);
        buttons.add(refresh);


        JTextArea textArea = new JTextArea();
        textArea.setText("Hello");
        textArea.setPreferredSize(new Dimension(500, 500));
        JPanel centraPanel = new JPanel(new BorderLayout());
        centraPanel.add(topicsPanel, BorderLayout.WEST);
        centraPanel.add(textArea, BorderLayout.EAST);
        textArea.setBackground(Color.CYAN);

        add(centraPanel, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        subscribeButton.addActionListener(e -> {
            try {
                System.out.println(client.subscribe(topicsList.getSelectedValue()));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        unsubscribeButton.addActionListener(e -> {
            try {
                System.out.println(client.unsubscribe(subscribedList.getSelectedValue()));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        refresh.addActionListener(e -> {
            try {
                subscribedList.setListData(client.getSubscribed());
                topicsList.setListData(client.getTopics());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        setVisible(true);
    }
}
