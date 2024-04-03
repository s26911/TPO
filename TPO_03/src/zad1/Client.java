package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {

    String mainServerAddr;
    int mainServerPort;

    public Client(String mainServerAddr, int mainServerPort) {
        this.mainServerAddr = mainServerAddr;
        this.mainServerPort = mainServerPort;
    }

    public void translate(String word, String langCode, int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket = new Socket(mainServerAddr, mainServerPort);
            new PrintWriter(socket.getOutputStream(), true).println(word + " " + langCode + " " + port);
            socket.close();
            System.out.println("CLIENT WAITING ON: " + serverSocket);
            Socket socket1 = serverSocket.accept();
            System.out.println("CLIENT ACCEPTED CONN FROM: " + socket1);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
            String line = reader.readLine();
            System.out.println(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
