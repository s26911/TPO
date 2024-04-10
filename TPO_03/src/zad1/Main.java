package zad1;

import java.net.*;

public class Main {
    static int mainServListeningPort = 50000;
    static int dictServListeningPort = 50001;
    static int clientListeningPort = 50010;


    public static void main(String[] args) throws UnknownHostException {
        MainServer mainServer = new MainServer(mainServListeningPort);
        DictServer dictServer = new DictServer("ENG", dictServListeningPort);
        DictServer dictServer2 = new DictServer("JAP", dictServListeningPort + 1);
        Client client = new Client("localhost", mainServListeningPort, clientListeningPort);

        mainServer.start();
        dictServer2.joinMainServer("localhost", mainServListeningPort);
        dictServer.joinMainServer("localhost", mainServListeningPort);
        dictServer.addTranslation("dom", "house");
        dictServer.start();
        dictServer2.start();

        UI ui = new UI(client);
    }
}


