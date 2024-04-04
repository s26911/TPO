package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

public class MainServer extends Thread {
    int listeningPort;
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
                // maybe add timeout
                Socket socket = serverSocket.accept();
                this.new MainServerTask(socket).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addDict(String langCode, String address, int port) {
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
                String[] split = line.split(" ");
//            System.out.println("LINE: " + line);
                // DICTIONARY SERVER REQUEST TO CONNECT: INCOMING DICT {langCode} {listeningPort}
                if (line.startsWith("INCOMING DICT"))
                    addDict(split[2], socket.getInetAddress().getHostAddress(), Integer.parseInt(split[3]));
                    // CLIENT TRANSLATION REQUEST: {wordToTranslate} {langCode} {clientsListeningPort}
                else if (line.startsWith("LIST")){
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(dictServers.keySet().stream().reduce("", (a, b) -> b + " " + a));
                }
                else {
                    if (!dictServers.containsKey(split[1]))
                        System.out.println("BRAK SLOWNIKA O DANYM KODZIE");
                    else {
                        String[] addrAndPort = dictServers.get(split[1]);
//                        System.out.println("TRY TO CONNECT TO DICT ON: " + addrAndPort[0] + " " + addrAndPort[1]);
                        Socket dict = new Socket(addrAndPort[0], Integer.parseInt(addrAndPort[1]));

                        // THIS TASKS REQUEST TO DICT SERVER: {wordToTranslate} {clientsIPAddr} {clientsListeningPort}
                        new PrintWriter(dict.getOutputStream(), true).println(split[0] + " " +
                                socket.getInetAddress().getHostAddress() + " " + split[2]);
                        dict.close();
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
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

