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
    int listeningPort;

    public Client(String mainServerAddr, int mainServerPort, int listeningPort) {
        this.mainServerAddr = mainServerAddr;
        this.mainServerPort = mainServerPort;
        this.listeningPort = listeningPort;
    }

    public String translate(String word, String langCode) {
        try (ServerSocket serverSocket = new ServerSocket(listeningPort)) {
            Socket socket = new Socket(mainServerAddr, mainServerPort);
            new PrintWriter(socket.getOutputStream(), true).println(word + " " + langCode + " " + listeningPort);
            socket.close();

//            System.out.println("CLIENT WAITING ON: " + serverSocket);
            Socket incoming = serverSocket.accept();
//            System.out.println("CLIENT ACCEPTED CONN FROM: " + incoming);
            BufferedReader reader = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] askForLanguages(){
        try (Socket socket = new Socket(mainServerAddr, mainServerPort)) {
            new PrintWriter(socket.getOutputStream(), true).println("LIST " + listeningPort);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return reader.readLine().trim().split(" ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
