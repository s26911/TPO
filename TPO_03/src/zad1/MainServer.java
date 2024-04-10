package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

public class MainServer extends Thread {
    private final int serverSocketSOTimeout = 30000;
    private final int listeningPort;
    boolean isRunning = true;
    HashMap<String, String[]> dictServers;          // { langCode, { address, port } }

    public MainServer(int listeningPort) {
        this.listeningPort = listeningPort;
        dictServers = new HashMap<>();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(listeningPort)) {
            while (isRunning) {
                serverSocket.setSoTimeout(serverSocketSOTimeout);
                Socket socket = serverSocket.accept();
                this.new MainServerTask(socket).start();
            }
        } catch (SocketTimeoutException ignored) {      // break loop to check if isRunning
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addDict(String langCode, String address, int port) {
        if(!dictServers.containsKey(langCode))
            dictServers.put(langCode, new String[]{address, Integer.toString(port)});
    }


    private class MainServerTask extends Thread {
        Socket socket;

        public MainServerTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = reader.readLine();
                String[] requestData = line.split(" ");

                if (line.startsWith("INCOMING DICT"))
                // DICTIONARY SERVER REQUEST TO CONNECT: INCOMING DICT {langCode} {listeningPort}
                    addDict(requestData[2], socket.getInetAddress().getHostAddress(), Integer.parseInt(requestData[3]));
                else if (line.startsWith("LIST")) {
                // CLIENT DICTIONARIES LIST REQUEST
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(dictServers.keySet().stream().reduce("", (a, b) -> b + " " + a));
                } else {
                    handleTranslationRequest(requestData);
                }

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

        private void handleTranslationRequest(String[] requestData) throws IOException {
            // CLIENT TRANSLATION REQUEST: {wordToTranslate} {langCode} {clientsListeningPort}
            String[] addrAndPort = dictServers.get(requestData[1]);
            if (addrAndPort == null){
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("NO AVAILABLE DICTIONARY FOR LANGUAGE CODE " + requestData[1]);
            }
            else {
                Socket dict = new Socket(addrAndPort[0], Integer.parseInt(addrAndPort[1]));
                // THIS TASKS REQUEST TO DICT SERVER: {wordToTranslate} {clientsIPAddr} {clientsListeningPort}
                new PrintWriter(dict.getOutputStream(), true).println(requestData[0] + " " +
                        socket.getInetAddress().getHostAddress() + " " + requestData[2]);
                dict.close();
            }
        }
    }
}

