package com.devops.chess.controllers;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.devops.chess.models.User;
import com.devops.chess.utils.FileHandler;

import java.io.IOException;
import java.util.List;

public class AuthController {

    private final Scene scene;
    private final Stage stage;
    private Label errorLabel;
    private TextField usernameField;
    private PasswordField passwordField;

    public AuthController(Stage stage) {
        this.stage = stage;
        this.scene = createLoginScene();
    }

    private Scene createLoginScene() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20; -fx-font-family: Arial;");

        Label titleLabel = new Label("Bienvenue dans ÉchecsGame");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");

        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button loginButton = new Button("Se connecter");
        loginButton.setOnAction(e -> handleLogin());

        Button registerButton = new Button("Créer un compte");
        registerButton.setOnAction(e -> handleRegister());

        HBox buttonBox = new HBox(10, loginButton, registerButton);
        buttonBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(titleLabel, usernameField, passwordField, errorLabel, buttonBox);

        return new Scene(root, 400, 300);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        List<User> users = FileHandler.loadUsers();

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                loadChessGame();
                return;
            }
        }

        errorLabel.setText("Nom d'utilisateur ou mot de passe incorrect.");
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Tous les champs doivent être remplis.");
            return;
        }

        FileHandler.saveUser(new User(username, password));
        errorLabel.setText("Compte créé avec succès !");
    }

    private void loadChessGame() {
        ChessController chessController = new ChessController(stage);
        stage.setScene(chessController.getScene());
    }

    public Scene getScene() {
        return scene;
    }
}
