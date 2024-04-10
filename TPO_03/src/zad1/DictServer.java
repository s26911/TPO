package zad1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class DictServer extends Thread {
    volatile HashMap<String, String> dict;           // { Polish word, translation }
    String langCode;
    int listeningPort;
    boolean isRunning = true;

    public DictServer(HashMap<String, String> dict, String langCode, int ListeningPort) {
        this.dict = dict;
        this.langCode = langCode;
        this.listeningPort = ListeningPort;
    }

    public DictServer(String langCode, int listeningPort) {
        this.langCode = langCode;
        this.listeningPort = listeningPort;
        this.dict = new HashMap<>();
    }

    public void joinMainServer(String address, int port) {
        try (Socket socket = new Socket(address, port)) {
            new PrintWriter(socket.getOutputStream(), true).println("INCOMING DICT " + langCode + " " + this.listeningPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addTranslation(String key, String value) {
        dict.put(key, value);
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(listeningPort)) {
            while (isRunning) {
                Socket socket = serverSocket.accept();
                this.new DictServerTask(socket).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class DictServerTask extends Thread {
        Socket socket;

        public DictServerTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // INCOMING TRANSLATION REQUEST FROM MAIN SERVER: {wordToTranslate} {clientsIPAddr} {clientsListeningPort}
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = reader.readLine();
                String[] split = line.split(" ");

                if(line.startsWith("PING")) {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("PONG");
                    return;
                }

                String result = dict.getOrDefault(split[0], "NO TRANSLATION");
                Socket toClient = new Socket(split[1], Integer.parseInt(split[2]));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(toClient.getOutputStream()), true);
                writer.println(result);

                toClient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

