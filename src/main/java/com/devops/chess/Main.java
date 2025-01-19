package com.devops.chess;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.devops.chess.controllers.AuthController;
import com.devops.chess.network.Client;

public class Main extends Application {

    private static final int SERVER_PORT = 8080;
    private static boolean isLaunched = false; // Indique si l'application JavaFX a démarré

    @Override
    public void start(Stage stage) {
        // Initialisation de l'interface de connexion
        AuthController authController = new AuthController(stage);
        Scene scene = authController.getScene();
        stage.setTitle("Jeu d'Échecs - Connexion");
        stage.setScene(scene);
        stage.show();
    }

    public static synchronized void launchJavaFX(String[] args) {
        if (!isLaunched) {
            isLaunched = true;
            launch(args);
        }
    }

    public static void main(String[] args) {
        // Connexion au serveur
        Client client = new Client("localhost", SERVER_PORT);
        if (client.connectToServer()) {
            client.listenForMessages();
        } else {
            System.err.println("Impossible de se connecter au serveur.");
        }
    }
}
