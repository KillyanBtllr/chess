package com.devops.chess.network;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private List<ClientHandler> clients;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket clientSocket, List<ClientHandler> clients) {
        this.clientSocket = clientSocket;
        this.clients = clients;
        try {
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Erreur lors de l'initialisation des flux pour le client : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Message reçu : " + message);
                broadcastMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Erreur dans la communication avec le client : " + e.getMessage());
        } finally {
            disconnectClient();
        }
    }

    private void broadcastMessage(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != this) {
                    client.sendMessage(message);
                }
            }
        }
    }

    private void sendMessage(String message) {
        out.println(message);
    }

    private void disconnectClient() {
        try {
            synchronized (clients) {
                clients.remove(this);
            }
            clientSocket.close();
            System.out.println("Client déconnecté : " + clientSocket.getInetAddress());
        } catch (IOException e) {
            System.err.println("Erreur lors de la déconnexion du client : " + e.getMessage());
        }
    }

    // Méthode pour démarrer le jeu
    public void startGame() {
        sendMessage("Démarrez le jeu !");  // Vous pouvez ajuster le message ici
    }
}
