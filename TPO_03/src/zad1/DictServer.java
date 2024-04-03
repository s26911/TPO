package zad1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class DictServer extends Thread {
    HashMap<String, String> dict;
    String langCode;
    int port;
    boolean isRunning = true;

    public DictServer(HashMap<String, String> dict, String langCode, int port) {
        this.dict = dict;
        this.langCode = langCode;
        this.port = port;
    }

    public void joinMainServer(String address, int port) {
        try (Socket socket = new Socket(address, port)) {
            new PrintWriter(socket.getOutputStream(), true).println("INCOMING DICT " + langCode + " " + this.port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (isRunning) {
                Socket socket = serverSocket.accept();
                new DictServerTask(socket, this).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class DictServerTask extends Thread {
    Socket socket;
    DictServer dictServer;

    public DictServerTask(Socket socket, DictServer dictServer) {
        this.socket = socket;
        this.dictServer = dictServer;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = reader.readLine();
            System.out.println("DICT TASK LINE: " + line);
            String[] data = line.split(" ");
            String translation = dictServer.dict.get(data[0]);
            Socket toClient = new Socket("localhost", Integer.parseInt(data[2]));
            System.out.println("DICT TASK REQUEST SOCKET: " + toClient);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(toClient.getOutputStream()), true);
            writer.println(translation);
            toClient.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}