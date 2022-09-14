package chat.server;

import network.TCPConnection;
import network.TCPConnectionListner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Properties;

public class ChatServer implements TCPConnectionListner {
//    public static final int PORT = 8189;

    public static void main(String[] args) {
        String parametr = args[0];
        new ChatServer(parametr);

    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer(String parametr) {
        System.out.println("Server Running...");
        try (InputStream input = new FileInputStream(parametr)) {

            Properties prop = new Properties();
            prop.load(input);
            String port = prop.getProperty("client.port");
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port));
                while (true) {
                    try {
                        new TCPConnection(this, serverSocket.accept());
                    } catch (IOException e) {
                        System.out.println("TCPConnection exception: " + e);
                    }
                }
            } catch(IOException e){
                throw new RuntimeException(e);
            }

        }

        @Override
        public synchronized void onConnectionReady (TCPConnection tcpConnection){
            connections.add(tcpConnection);
            sendToAllConnections("Client connected: " + tcpConnection);
        }

        @Override
        public synchronized void onReceiveString (TCPConnection tcpConnection, String value){
            sendToAllConnections(value);
        }

        @Override
        public synchronized void onDisconnect (TCPConnection tcpConnection){
            connections.remove(tcpConnection);
            sendToAllConnections("Client disconnected: " + tcpConnection);
        }

        @Override
        public synchronized void onException (TCPConnection tcpConnection, Exception e){
            System.out.println("TCPConnection exception: " + e);
        }

        private void sendToAllConnections (String value){
            System.out.println(value);
            final int cnt = connections.size();
            for (int i = 0; i < cnt; i++) connections.get(i).sendString(value);

        }
    }


