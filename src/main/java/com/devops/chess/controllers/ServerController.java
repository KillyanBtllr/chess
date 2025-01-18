package com.devops.chess.controllers;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ServerController {
    private static final int MAX_PLAYERS = 2; // Limite de joueurs
    private int port;
    private ServerSocket serverSocket;
    private Map<Integer, PrintWriter> clients = new HashMap<>();
    private int currentPlayerId = 1; // Compteur pour assigner les rôles

    public ServerController(int port) {
        this.port = port;
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Serveur lancé. En attente de clients...");
            while (clients.size() < MAX_PLAYERS) {
                // Accepter les connexions des clients
                Socket clientSocket = serverSocket.accept();
                System.out.println("Un client est connecté !");
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                int playerId = currentPlayerId++;
                clients.put(playerId, out);

                // Attribuer un rôle à chaque joueur
                if (playerId == 1) {
                    out.println("Vous êtes Joueur 1. En attente du second joueur...");
                } else {
                    out.println("Vous êtes Joueur 2. En attente du premier joueur...");
                }

                // Si deux joueurs sont connectés, démarrer le jeu
                if (clients.size() == MAX_PLAYERS) {
                    startGame();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startGame() {
        System.out.println("Deux joueurs sont connectés, le jeu commence !");
        // Envoyer à chaque joueur qu'il y a assez de joueurs pour commencer
        for (Map.Entry<Integer, PrintWriter> entry : clients.entrySet()) {
            entry.getValue().println("Le jeu commence !");
        }

        // Vous pouvez aussi ici démarrer la logique du jeu (envoi des coups, etc.)
    }

    public void broadcastMessage(String message) {
        for (PrintWriter client : clients.values()) {
            client.println(message);
        }
    }

    public void stopServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour obtenir les PrintWriter des joueurs, pour envoyer des messages spécifiques
    public PrintWriter getPlayerOutput(int playerId) {
        return clients.get(playerId);
    }
}
