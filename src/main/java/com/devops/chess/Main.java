package com.devops.chess;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.devops.chess.controllers.AuthController;
import com.devops.chess.network.Client;

public class Main extends Application implements Client.GameStartListener {

    private static final int SERVER_PORT = 8080;

    @Override
    public void start(Stage stage) {
        // Initialisation de l'interface de connexion
        AuthController authController = new AuthController(stage);
        Scene scene = authController.getScene();
        stage.setTitle("Jeu d'Échecs - Connexion");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void init() throws Exception {
        // Connexion au serveur
        Client client = new Client("localhost", SERVER_PORT, this);  // Passer la référence de Main à Client
        if (client.connectToServer()) {
            client.listenForMessages();
        } else {
            System.err.println("Impossible de se connecter au serveur.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void onGameStart() {
        // Cette méthode est appelée lorsque le message de démarrage du jeu est reçu
        System.out.println("Le jeu commence maintenant !");
        // Lancez l'interface de jeu ici
        // Si vous souhaitez démarrer un autre type d'interface ou initialiser des scènes, vous pouvez appeler "launch"
        Platform.runLater(() -> {
            // Initialisez l'interface du jeu ici, vous pouvez charger une nouvelle scène
            Stage stage = new Stage();
            // Exemple : changez la scène actuelle pour une scène de jeu
            stage.setTitle("Jeu d'Échecs");
            stage.show();
        });
    }
}
