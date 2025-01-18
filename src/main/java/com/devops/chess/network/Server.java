package com.devops.chess.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static List<ClientHandler> clients = new ArrayList<>();
    private static boolean isRunning = false;

    public static void main(String[] args) {
        int port = 8080; // Port par défaut
        launchServer(port);
    }

    public static void launchServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur démarré sur le port " + port);
            isRunning = true;

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, clients);
                clients.add(clientHandler);
                new Thread(clientHandler).start();

                // Vérifiez si 2 joueurs sont connectés
                if (clients.size() == 2) {
                    // Informez les deux clients de commencer le jeu
                    startGameForClients();
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du démarrage du serveur : " + e.getMessage());
        }
    }

    private static void startGameForClients() {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                // Envoie d'un message ou d'une commande pour démarrer le jeu
                client.startGame();
            }
        }
    }

    public static boolean isServerRunning() {
        return isRunning;
    }
}
