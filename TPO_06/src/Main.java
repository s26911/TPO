import org.exolab.jms.administration.AdminConnectionFactory;
import org.exolab.jms.administration.JmsAdminServerIfc;

import javax.jms.JMSException;
import java.net.MalformedURLException;

public class Main {
    public static void main(String[] args) throws JMSException, MalformedURLException {
        String url = "tcp://localhost:3035/";
        JmsAdminServerIfc admin = AdminConnectionFactory.create(url);

        String[] topics = {
                "general", "topic1", "topic2", "topic3", "topic4"
        };
        Boolean isQueue = Boolean.FALSE;
        for (String topic : topics) {
            if (!admin.destinationExists(topic) && !admin.addDestination(topic, isQueue)) {
                System.err.println("Failed to create topic " + topic);
            }
        }

        new Client(topics);
    }
}
