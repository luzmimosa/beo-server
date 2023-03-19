package com.fadedbytes.beo.network.socket;

import com.fadedbytes.beo.network.NetworkManager;
import com.fadedbytes.beo.network.packet.RawNetworkPacket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public final class SocketController {

    private static final int MAX_WAITING_CONNECTIONS = 50;
    public static final ThreadGroup LISTENING_SOCKETS_THREAD_GROUP = new ThreadGroup("ListeningSockets");

    public final int PORT;
    private final NetworkManager networkManager;
    private final ServerSocket serverSocket;
    private final Thread listeningThread;
    private boolean listening;

    /**
     * Creates a new SocketController instance.
     *
     * @param port The port to listen on.
     * @throws IOException If an I/O error occurs when opening the socket.
     */
    public SocketController(
            String address,
            int port,
            NetworkManager networkManager
    ) throws IOException {
        this.PORT = port;
        this.networkManager = networkManager;
        this.listening = true;

        InetAddress inetAddress = InetAddress.getByName(address);
        serverSocket = ServerSocketFactory.getDefault().createServerSocket(port, MAX_WAITING_CONNECTIONS, inetAddress);

        this.listeningThread = new Thread(LISTENING_SOCKETS_THREAD_GROUP, this::listen, "ListeningSocket-" + port);
        this.listeningThread.start();
    }

    private void listen() throws WrongThreadException {
        if (Thread.currentThread().getThreadGroup() != LISTENING_SOCKETS_THREAD_GROUP) {
            throw new WrongThreadException("SocketController listening thread must be in the ListeningSockets thread group.");
        }

        while (listening) {
            try {
                Socket clientSocket = serverSocket.accept();

                InputStream socketInputStream = clientSocket.getInputStream();
                OutputStream socketOutputStream = clientSocket.getOutputStream();

                this.networkManager.receivePacket(
                        new RawNetworkPacket(
                                clientSocket.getRemoteSocketAddress(),
                                socketInputStream,
                                socketOutputStream
                        )
                );

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stopListening() {
        listening = false;
    }

}
