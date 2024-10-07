package org.example.chat.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private final Socket socket;
    private final String username;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    public Client(Socket socket, String username) throws IOException {
        this.socket = socket;
        this.username = username;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                while (socket.isConnected()) {
                    try {
                        message = bufferedReader.readLine();
                        System.out.println(message);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                        break;
                    }
                }
            }
        }).start();
    }

    public void sendMessage() throws IOException {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                String[] chars = message.split("");
                if (chars[0].equals("/") && chars[1].equals("@") && chars[2].equals(" ")) {
                    String nameOfRecipient = message.split("")[0];
                    
                } else {
                    bufferedWriter.write(username + ": " + message);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {

            if (socket != null) socket.close();
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}