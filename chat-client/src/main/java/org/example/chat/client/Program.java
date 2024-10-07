package org.example.chat.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            String username = "";
            while (username.isEmpty() || username.split(" ").length == 0 || username.split("")[0].equals(" ")) {
                System.out.print("Введите свое имя: ");
                username = scanner.nextLine();
            }
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