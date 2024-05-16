import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.function.Consumer;

public class AdministratorGUI extends JFrame {
    Administrator admin;
    JComponent[] active = new JComponent[2];

    public static void main(String[] args) {
        new AdministratorGUI(new Administrator("localhost", 50000));
    }

    public AdministratorGUI(Administrator admin) {
        this.admin = admin;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Administrator panel");
        int width = 900, height = 800;
        setSize(width, height);
        setLayout(new GridBagLayout());
        GridBagConstraints constr = new GridBagConstraints();

        // Mode selection menu
        JButton sendModeButton = new JButton("Send Mode");
        JButton addTopicModeButton = new JButton("Add Topic Mode");
        JButton delTopicModeButton = new JButton("Del Topic Mode");
        JPanel modeSelection = new JPanel();
        FlowLayout layout = new FlowLayout();
        layout.setVgap(180);
        modeSelection.setLayout(layout);
        modeSelection.setPreferredSize(new Dimension(width/6, height));
        modeSelection.add(sendModeButton);
        modeSelection.add(addTopicModeButton);
        modeSelection.add(delTopicModeButton);

        // Shared components
        JButton actionButton = new JButton("Action");
        JButton refreshButton = new JButton("Refresh");
        JTextArea console = new JTextArea();
        console.setEditable(false);
        console.setPreferredSize(new Dimension(width/6 * 5, height/8 * 2));
        JList<String> topics = new JList<>();
        topics.setPreferredSize(new Dimension(width/6 * 2, height/8 * 5));

        // SEND mode
        JTextField textField = new JTextField();

        // ADDTOPIC mode
        JTextField topicInputField = new JTextField();

        // Add components
        constr.gridx = 0;
        constr.gridy = 0;
        constr.gridwidth = 1;
        constr.gridheight = 8;
        add(modeSelection, constr);

        constr.gridx = 2;
        constr.gridy = 5;
        constr.gridwidth = 1;
        constr.gridheight = 1;
        add(actionButton, constr);

        constr.gridx = 5;
        constr.gridy = 5;
        constr.gridwidth = 1;
        constr.gridheight = 1;
        add(refreshButton, constr);

        constr.gridx = 1;
        constr.gridy = 6;
        constr.gridwidth = 5;
        constr.gridheight = 2;
        add(console, constr);

        // SEND mode (default)

        ActionListener[] actions = {
                e -> {
                    String topic = topics.getSelectedValue();
                    String text = textField.getText().strip();
                    if (topic == null)
                        return;
                    try {
                        admin.send(topic, text);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                e -> {
                    String topic = textField.getText().strip();
                    try {
                        admin.addTopic(topic);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                e -> {
                    String topic = topics.getSelectedValue();
                    try {
                        admin.delTopic(topic);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
        };

        // Action listeners
        sendModeButton.addActionListener(e -> {
            for (var elem : active){
                if (elem != null)
                    remove(elem);
            }
            for (var elem : actionButton.getActionListeners()){
                actionButton.removeActionListener(elem);
            }
            actionButton.addActionListener(actions[0]);
            actionButton.setText("Send");
            textField.setPreferredSize(new Dimension(width/6 * 3, height/8 * 5));

            constr.gridx = 1;
            constr.gridy = 0;
            constr.gridwidth = 2;
            constr.gridheight = 5;
            add(topics, constr);
            active[0] = topics;

            constr.gridx = 3;
            constr.gridy = 0;
            constr.gridwidth = 3;
            constr.gridheight = 5;
            add(textField, constr);
            active[1] = textField;
        });
        addTopicModeButton.addActionListener(e -> {
            for (var elem : active){
                if (elem != null)
                    remove(elem);
            }
            for (var elem : actionButton.getActionListeners()){
                actionButton.removeActionListener(elem);
            }
            actionButton.addActionListener(actions[1]);
            actionButton.setText("Add");
            textField.setPreferredSize(new Dimension(width/6 * 3, height/8 * 5));

            constr.gridx = 1;
            constr.gridy = 0;
            constr.gridwidth = 2;
            constr.gridheight = 5;
            add(topics, constr);
            active[0] = topics;

            constr.gridx = 3;
            constr.gridy = 0;
            constr.gridwidth = 3;
            constr.gridheight = 5;
            add(textField, constr);
            active[1] = textField;
        });
        delTopicModeButton.addActionListener(e -> {
            for (var elem : active){
                if (elem != null)
                    remove(elem);
            }
            for (var elem : actionButton.getActionListeners()){
                actionButton.removeActionListener(elem);
            }
            actionButton.addActionListener(actions[2]);
            actionButton.setText("Delete");

            constr.gridx = 1;
            constr.gridy = 0;
            constr.gridwidth = 5;
            constr.gridheight = 5;
            add(topics, constr);
            active[0] = topics;
            active[1] = null;
        });
        refreshButton.addActionListener(e -> {
            try {
                String[] data = admin.getTopics();
                topics.setListData(data);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });


        sendModeButton.doClick();
//        pack();
        setVisible(true);
    }
}
