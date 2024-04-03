package zad1;

import java.io.*;
import java.net.*;
import java.util.HashMap;


public class Main {
    public static void main(String[] args) throws UnknownHostException {
        MainServer mainServer = new MainServer(50000);
        HashMap<String, String> plEng = new HashMap<>();
        plEng.put("dom", "house");
        DictServer dictServer = new DictServer(plEng, "ENG", 50001);
        Client client = new Client("localhost", 50000);
        mainServer.start();
        dictServer.joinMainServer("localhost", 50000);
        dictServer.start();
        client.translate("dom", "ENG", 50002);
    }
}


