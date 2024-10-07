package org.example.chat.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите свое имя: ");
            String username = scanner.nextLine();
            Socket socket = new Socket("localhost", 1400);
            Client client = new Client(socket, username);

            client.listenForMessage();
            client.sendMessage();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}