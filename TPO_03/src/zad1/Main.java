package zad1;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;


public class Main {
    static int mainServListeningPort = 50000;
    static int dictServListeningPort = 50001;
    static int clientListeningPort = 50002;


    public static void main(String[] args) throws UnknownHostException {
        MainServer mainServer = new MainServer(mainServListeningPort);
        DictServer dictServer = new DictServer("ENG", dictServListeningPort);
        DictServer dictServer2 = new DictServer("JAP", dictServListeningPort + 5);
        Client client = new Client("localhost", mainServListeningPort, clientListeningPort);

        mainServer.start();
        dictServer2.joinMainServer("localhost", mainServListeningPort);
        dictServer.joinMainServer("localhost", mainServListeningPort);
        dictServer.addTranslation("dom", "house");
        dictServer.start();
        dictServer2.start();

//        client.translate("dom", "ENG");
        UI ui = new UI(client);
    }
}


