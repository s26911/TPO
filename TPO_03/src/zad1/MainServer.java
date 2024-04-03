package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class MainServer extends Thread {
    int port;
    boolean isRunning = true;
    HashMap<String, Object[]> dictServers;

    public MainServer(int port) {
        this.port = port;
        dictServers = new HashMap<>();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (isRunning) {
                System.out.println("MAIN SERV WAITING...");                     //
                Socket socket = serverSocket.accept();
                System.out.println("MAIN SERVER ACCEPTED: " + socket.toString()); //
                new MainServerTask(socket, this).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addDict(String langCode, String address, int port) {
        dictServers.put(langCode, new Object[]{address, port});
    }

}

class MainServerTask extends Thread {
    Socket socket;
    MainServer mainServer;

    public MainServerTask(Socket socket, MainServer mainServer) {
        this.socket = socket;
        this.mainServer = mainServer;
    }

    @Override
    public void run() {
        try {
            System.out.println("NEW TASK");                     //
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = reader.readLine();
            System.out.println("LINE: " + line);
            if (line.startsWith("INCOMING DICT"))
                mainServer.addDict(line.split(" ")[2], socket.getInetAddress().getHostAddress(), Integer.parseInt(line.split(" ")[3]));
            else {
                String[] data = line.split(" ");
                if (!mainServer.dictServers.containsKey(data[1]))
                    System.out.println("BRAK SLOWNIKA O DANYM KODZIE");
                else {
                    Object[] addr = mainServer.dictServers.get(data[1]);
                    System.out.println("TRY TO CONNECT TO DICT ON: " + addr[0].toString() + " " + addr[1].toString());
                    Socket dict = new Socket((String) addr[0], (int) addr[1]);
                    new PrintWriter(dict.getOutputStream(), true).println(data[0] + " " +
                            socket.getInetAddress() + " " + line.split(" ")[2]);
                    dict.close();
                }
            }

            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}