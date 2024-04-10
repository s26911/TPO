package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

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

            serverSocket.setSoTimeout(5000);
            Socket incoming = serverSocket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
            return reader.readLine();
        }catch (SocketTimeoutException e){
            return "TIMEOUT";
        }
        catch (IOException e) {
            return "CONNECTION TO THE MAIN SERVER FAILED";
        }
    }

    public String[] askForLanguages(){
        try (Socket socket = new Socket(mainServerAddr, mainServerPort)) {
            new PrintWriter(socket.getOutputStream(), true).println("LIST " + listeningPort);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return reader.readLine().trim().split(" ");
        } catch (IOException e) {
            return new String[0];
        }
    }
}
