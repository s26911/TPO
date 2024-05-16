import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ClientGUI extends JFrame {

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
        int width = 900, height = 600;
        setSize(width, height);
        setLayout(new GridBagLayout());
        GridBagConstraints constr = new GridBagConstraints();

        subscribed = client.getSubscribed();
        topics = client.getTopics();
//        subscribed = new String[10];
//        topics = new String[10];

        JList<String> subscribedList = new JList<>(subscribed);
//        subscribedList.setPreferredSize(new Dimension(width / 2, 350));
        JList<String> topicsList = new JList<>(topics);
//        topicsList.setPreferredSize(new Dimension(width / 2, 350));
        JPanel topicsPanel = new JPanel();
        topicsPanel.setPreferredSize(new Dimension(width, height/3));
        topicsPanel.add(subscribedList);
        topicsPanel.add(topicsList);
//        topicsPanel.setBackground(Color.BLUE);

        JPanel buttons = new JPanel();
//        buttons.setPreferredSize(new Dimension(width, 100));
        JButton subscribeButton = new JButton("Subscribe");
        JButton unsubscribeButton = new JButton("Unsubscribe");
        JButton refresh = new JButton("Refresh");
        buttons.add(subscribeButton);
        buttons.add(unsubscribeButton);
        buttons.add(refresh);
//        buttons.setBackground(Color.RED);


        JTextArea textArea = new JTextArea();
        textArea.setText("Hello");
//        textArea.setPreferredSize(new Dimension(width, 150));
//        textArea.setBackground(Color.GREEN);

        constr.gridx = 0;
        constr.gridy = 0;
        constr.weighty = 0.6;
        constr.gridwidth = 1;
        constr.gridheight = 1;
        add(topicsPanel, constr);

        constr.gridy = 1;
        constr.weighty = 0.1;
        add(buttons, constr);

        constr.gridy = 2;
        constr.weighty = 0.3;
        add(textArea, constr);

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
