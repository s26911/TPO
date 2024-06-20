import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;

import javax.jms.*;
import javax.naming.*;

public class Client extends JFrame implements MessageListener {
    private CountDownLatch latch = new CountDownLatch(1);
    private Connection con;
    private JTextArea chatMessagesPane = new JTextArea(10, 20);
    private Session ses;
    private MessageProducer producer;
    private String topic, name;

    public Client(String[] topics) throws HeadlessException {
        // Welcome dialog to input name and pick a topic
        JDialog welcome = new JDialog(this);
        welcome.setLayout(new GridLayout(5,1));
        welcome.add(new JLabel("Input your name:",JLabel.CENTER));
        JTextField name = new JTextField();
        welcome.add(name);

        welcome.add(new JLabel("Select a topic:",JLabel.CENTER));
        JList<String> topicsList = new JList<>(topics);
        JScrollPane topicsPane = new JScrollPane(topicsList);
        welcome.add(topicsPane);

        JButton next = new JButton("Next");
        welcome.add(next);
        next.addActionListener(e -> {
            this.topic = topicsList.getSelectedValue();
            this.name = name.getText();
            latch.countDown();
            welcome.dispose();
        });
        welcome.setTitle("Welcome!");
        welcome.setSize(300,300);
        welcome.setVisible(true);

        // Waiting for user to click a button
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Further configuration
        try {
            Hashtable env = new Hashtable(11);
            env.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
            env.put(Context.PROVIDER_URL, "tcp://localhost:3035");

            Context ctx = new InitialContext(env);
            ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
            Destination dest = (Destination) ctx.lookup(topic);
            con = factory.createConnection();
            ses = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer receiver = ses.createConsumer(dest);
            receiver.setMessageListener(this);
            producer = ses.createProducer(dest);
            con.start();
        } catch (Exception exc) {
            exc.printStackTrace();
            System.exit(1);
        }

        // Composing main chat window
        JPanel chatPanel = new JPanel(new GridLayout(2,1));
        chatMessagesPane.setEditable(false);
        chatPanel.add(new JScrollPane(chatMessagesPane));
        JTextArea messageInput = new JTextArea();
        chatPanel.add(messageInput);
        add(chatPanel, BorderLayout.CENTER);

        JButton but = new JButton("Send");
        but.addActionListener(e -> {
            try {
                producer.send(ses.createTextMessage(this.name + ": " + messageInput.getText().trim()));
            } catch (JMSException ex) {
                throw new RuntimeException(ex);
            }
            messageInput.setText("");
        });
        add(but, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    con.close();
                } catch (Exception exc) {
                }
                dispose();
                System.exit(0);
            }
        });
        setTitle(this.name + " is chatting on topic \"" + this.topic + "\"");
        setSize(400,400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    int i = 0;
    @Override
    public void onMessage(Message message) {
        try {
            chatMessagesPane.append(((TextMessage) message).getText() + "\n");
        } catch (JMSException exc) {
            System.err.println(exc);
        }
    }
}
