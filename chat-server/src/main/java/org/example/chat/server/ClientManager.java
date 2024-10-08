package org.example.chat.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable {

    private final Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String name;
    public final static ArrayList<ClientManager> clients = new ArrayList<>();

    public ClientManager(Socket socket) {
        this.socket = socket;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            name = bufferedReader.readLine();
            clients.add(this);
            System.out.println(name + " подключился к чату");
            broadcastMessage("Server: " + name + " подключился к чату.");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Проверка на появление новых сообщений
    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient == null) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
                if (messageFromClient.split(" ")[1].startsWith("/") &&
                        messageFromClient.split(" ")[1].split("")[1].equals("@")) {
                    String recipient = messageFromClient.split("@")[1].split(" ")[0];
                    String message = messageFromClient.split(" ")[2];
                    sendPrivateMessage(recipient, messageFromClient);
                } else {
                    broadcastMessage(messageFromClient);
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    private void sendPrivateMessage(String recipient, String message) {
        for (ClientManager clientManager : clients) {
            if (clientManager.name.equals(recipient)) {
                try {
                    clientManager.bufferedWriter.write(message);
                    clientManager.bufferedWriter.newLine();
                    clientManager.bufferedWriter.flush();
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
            }
        }
    }

    // Высвечивание сообщения отправителя для всех, кроме самого отправителя.
    private void broadcastMessage(String message) {
        for (ClientManager clientManager : clients) {
            if (!clientManager.name.equals(name)) {
                try {
                    clientManager.bufferedWriter.write(message);
                    clientManager.bufferedWriter.newLine();
                    clientManager.bufferedWriter.flush();
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
            }
        }
    }

    // Закрытие потоков
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClient();
        try {
            if (socket != null) socket.close();
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Удаление Клиента
    private void removeClient() {
        clients.remove(this);
        System.out.println(name + " покинул чат");
        broadcastMessage("Server: " + name + " покинул чат.");
    }

}