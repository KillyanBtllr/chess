package com.devops.chess.network;

import com.devops.chess.Main;

import java.io.*;
import java.net.Socket;

public class Client {

    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public boolean connectToServer() {
        try {
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connecté au serveur : " + serverAddress + ":" + serverPort);
            return true;
        } catch (IOException e) {
            System.err.println("Erreur de connexion au serveur : " + e.getMessage());
            return false;
        }
    }

    public void listenForMessages() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Message reçu du serveur : " + message);
                    if (message.equals("Démarrez le jeu !")) {
                        // Lancer le jeu
                        launch();
                    }
                }
            } catch (IOException e) {
                System.err.println("Erreur lors de la réception des messages : " + e.getMessage());
            } finally {
                disconnect();
            }
        }).start();
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
                System.out.println("Déconnecté du serveur.");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la déconnexion : " + e.getMessage());
        }
    }

    public void launch() {
        // Démarre JavaFX si ce n'est pas déjà fait
        Main.launchJavaFX(new String[]{});
        System.out.println("Le jeu commence maintenant !");
    }

}
