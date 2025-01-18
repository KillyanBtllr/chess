package com.devops.chess.network;

import java.io.*;
import java.net.Socket;

public class Client {

    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GameStartListener gameStartListener; // Interface pour notifier Main

    public Client(String serverAddress, int serverPort, GameStartListener gameStartListener) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.gameStartListener = gameStartListener;
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
                    if (message.equals("Le jeu commence maintenant !")) {
                        // Notifier Main.java pour démarrer le jeu
                        gameStartListener.onGameStart();
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

    public interface GameStartListener {
        void onGameStart(); // Méthode à implémenter dans Main.java pour démarrer le jeu
    }
}
