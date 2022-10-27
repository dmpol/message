package chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThread;
    private final TCPConnectionListener eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection(TCPConnectionListener eventListener, String ipAddr, int port) throws IOException {
        this(eventListener, new Socket(ipAddr, port));//вызываем конструктор ниже
    }

    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    eventListener.onConnectionReady(TCPConnection.this);//передали экземпляр обрамляющего класса
                    while (!rxThread.isInterrupted()){
                        String mag = in.readLine();
                        eventListener.onReceiveStrting(TCPConnection.this,mag);
                    }
                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();
    }

    public synchronized void sendString(String value){
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
