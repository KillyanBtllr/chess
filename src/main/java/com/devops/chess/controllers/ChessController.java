package com.devops.chess.controllers;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.devops.chess.models.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessController {

    private boolean isClient;
    private final Scene scene;
    private final Stage stage;
    private final GridPane chessBoard;
    private final Map<String, Piece> pieces = new HashMap<>(); // Stocke les pièces avec leurs positions
    private final Map<String, Color> squareBackgroundColors = new HashMap<>(); // Couleur de fond des cases
    private Piece selectedPiece = null; // La pièce sélectionnée
    private String selectedPosition = null; // Position de la pièce sélectionnée



    public ChessController(Stage stage) {
        this.stage = stage;
        this.chessBoard = new GridPane();
        this.scene = createChessScene();
        initializeChessBoard();
        placeInitialPieces();
        addBoardClickListener();
    }

    private Scene createChessScene() {
        chessBoard.setAlignment(Pos.CENTER);
        chessBoard.setGridLinesVisible(false);
        return new Scene(chessBoard, 640, 640);
    }

    private void initializeChessBoard() {
        int size = 8; // Taille de la grille d'échecs
        boolean isWhite = false;

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                StackPane square = new StackPane();
                Rectangle background = new Rectangle(80, 80);
                Color color = isWhite ? Color.BEIGE : Color.GRAY;
                background.setFill(color);
                square.getChildren().add(background);

                // Enregistrer la couleur de fond de chaque case
                squareBackgroundColors.put(col + "," + row, color);

                chessBoard.add(square, col, row);
                isWhite = !isWhite;
            }
            isWhite = !isWhite; // Alterner à la fin de chaque ligne
        }
    }

    private void placeInitialPieces() {
        // Ajouter les pièces blanches
        addPiece("rook", "white", 0, 0);
        addPiece("knight", "white", 1, 0);
        addPiece("bishop", "white", 2, 0);
        addPiece("queen", "white", 3, 0);
        addPiece("king", "white", 4, 0);
        addPiece("bishop", "white", 5, 0);
        addPiece("knight", "white", 6, 0);
        addPiece("rook", "white", 7, 0);
        for (int i = 0; i < 8; i++) {
            addPiece("pawn", "white", i, 1);
        }

        // Ajouter les pièces noires
        addPiece("rook", "black", 0, 7);
        addPiece("knight", "black", 1, 7);
        addPiece("bishop", "black", 2, 7);
        addPiece("queen", "black", 3, 7);
        addPiece("king", "black", 4, 7);
        addPiece("bishop", "black", 5, 7);
        addPiece("knight", "black", 6, 7);
        addPiece("rook", "black", 7, 7);
        for (int i = 0; i < 8; i++) {
            addPiece("pawn", "black", i, 6);
        }
    }

    private void addPiece(String type, String color, int col, int row) {
        Piece piece = new Piece(type, color);
        pieces.put(col + "," + row, piece);

        // Ajouter une représentation visuelle sur le plateau
        StackPane square = getSquareAt(col, row);
        if (square != null) {
            Text pieceSymbol = new Text(getSymbolForPiece(type, color));
            pieceSymbol.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");
            square.getChildren().add(pieceSymbol); // Ajouter la pièce au centre de la case
        }
    }

    private StackPane getSquareAt(int col, int row) {
        for (var node : chessBoard.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return (StackPane) node;
            }
        }
        return null;
    }

    private String getSymbolForPiece(String type, String color) {
        // Unicode pour les pièces d'échecs
        switch (type.toLowerCase()) {
            case "king":
                return color.equals("white") ? "♔" : "♚";
            case "queen":
                return color.equals("white") ? "♕" : "♛";
            case "rook":
                return color.equals("white") ? "♖" : "♜";
            case "bishop":
                return color.equals("white") ? "♗" : "♝";
            case "knight":
                return color.equals("white") ? "♘" : "♞";
            case "pawn":
                return color.equals("white") ? "♙" : "♟";
            default:
                return "?";
        }
    }

    // Ajout de la logique pour sélectionner et déplacer les pièces
    private void addBoardClickListener() {
        chessBoard.setOnMouseClicked(event -> {
            try {
                handleBoardClick(event);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Erreur lors de la gestion du clic sur le plateau.");
            }
        });
    }

    private void handleBoardClick(MouseEvent event) {
        int col = (int) (event.getSceneX() / 80); // Calcul de la colonne
        int row = (int) (event.getSceneY() / 80); // Calcul de la ligne

        String targetPosition = col + "," + row; // Position cible

        if (selectedPiece == null) {
            // Sélection d'une pièce à jouer
            if (pieces.containsKey(targetPosition)) {
                Piece piece = pieces.get(targetPosition);
                if (piece != null && piece.getColor().equals(currentTurn)) {
                    selectedPiece = piece;
                    selectedPosition = targetPosition;
                    highlightSelectedSquare(col, row);
                }
            }
        } else {
            // Déplacement d'une pièce
            if (isMoveValid(selectedPiece, col, row)) {
                // Vérifier si une pièce est capturée
                if (pieces.containsKey(targetPosition)) {
                    Piece capturedPiece = pieces.get(targetPosition);

                    // Si la pièce capturée est un roi, la partie se termine
                    if (capturedPiece.getType().equals("king")) {
                        System.out.println("Le roi a été capturé ! Partie terminée.");
                        endGame(currentTurn); // Méthode pour gérer la fin de la partie
                        return; // Arrêter toute autre action
                    }
                }

                // Effectuer le déplacement
                movePiece(col, row);

                // Changer de tour
                currentTurn = currentTurn.equals("white") ? "black" : "white";
            }

            // Réinitialiser la sélection
            selectedPiece = null;
            selectedPosition = null;
            resetHighlighting();
        }
    }





    private boolean isKingInCheck(String color) {
        // Trouver la position du roi de la couleur donnée
        String kingPosition = null;
        for (Map.Entry<String, Piece> entry : pieces.entrySet()) {
            if (entry.getValue().getType().equals("king") && entry.getValue().getColor().equals(color)) {
                kingPosition = entry.getKey();
                break;
            }
        }

        if (kingPosition == null) {
            return false; // Si le roi n'existe pas, pas d'échec
        }

        // Vérifier si une pièce adverse peut atteindre la position du roi
        for (Map.Entry<String, Piece> entry : pieces.entrySet()) {
            Piece piece = entry.getValue();
            if (!piece.getColor().equals(color)) {
                int targetCol = Integer.parseInt(kingPosition.split(",")[0]);
                int targetRow = Integer.parseInt(kingPosition.split(",")[1]);
                if (isMoveValid(piece, targetCol, targetRow)) {
                    return true; // Une pièce adverse peut atteindre le roi
                }
            }
        }

        return false; // Pas d'échec
    }
    private boolean isCheckmate(String color) {
        // Si le roi n'est pas en échec, il ne peut pas être en échec et mat
        if (!isKingInCheck(color)) {
            return false;
        }

        // Créer une liste des positions des pièces du joueur
        List<String> piecePositions = new ArrayList<>(pieces.keySet());

        // Vérifier si le joueur peut faire un mouvement légal pour sortir de l'échec
        for (String position : piecePositions) {
            Piece piece = pieces.get(position);
            if (piece != null && piece.getColor().equals(color)) {
                int currentCol = Integer.parseInt(position.split(",")[0]);
                int currentRow = Integer.parseInt(position.split(",")[1]);

                // Tester tous les mouvements possibles pour cette pièce
                for (int col = 0; col < 8; col++) {
                    for (int row = 0; row < 8; row++) {
                        if (isMoveValid(piece, col, row)) {
                            // Simuler le mouvement
                            Piece capturedPiece = pieces.remove(col + "," + row);
                            pieces.put(col + "," + row, piece);
                            pieces.remove(position);

                            // Vérifier si le roi est toujours en échec après ce mouvement
                            boolean stillInCheck = isKingInCheck(color);

                            // Annuler le mouvement
                            pieces.put(position, piece);
                            if (capturedPiece != null) {
                                pieces.put(col + "," + row, capturedPiece);
                            } else {
                                pieces.remove(col + "," + row);
                            }

                            if (!stillInCheck) {
                                return false; // Le joueur peut se sortir de l'échec
                            }
                        }
                    }
                }
            }
        }

        return true; // Aucun mouvement légal pour sortir de l'échec
    }


    private boolean isMoveValid(Piece piece, int col, int row) {
        String targetPosition = col + "," + row;

        // Vérifier si la case cible est occupée par une pièce de la même couleur
        if (pieces.containsKey(targetPosition) && pieces.get(targetPosition).getColor().equals(piece.getColor())) {
            return false;
        }

        // Logique pour les déplacements des pièces
        switch (piece.getType().toLowerCase()) {
            case "pawn":
                return isPawnMoveValid(piece, col, row);
            case "rook":
                return isRookMoveValid(piece, col, row);
            case "knight":
                return isKnightMoveValid(piece, col, row);
            case "bishop":
                return isBishopMoveValid(piece, col, row);
            case "queen":
                return isQueenMoveValid(piece, col, row);
            case "king":
                return isKingMoveValid(piece, col, row);
            default:
                return false;
        }
    }

    private boolean isPawnMoveValid(Piece piece, int col, int row) {
        // Récupérer la position actuelle du pion
        int currentCol = Integer.parseInt(selectedPosition.split(",")[0]);
        int currentRow = Integer.parseInt(selectedPosition.split(",")[1]);

        // Direction du déplacement selon la couleur (blanc : vers le bas, noir : vers le haut)
        int direction = piece.getColor().equals("white") ? 1 : -1;

        // Vérification du déplacement d'une case en avant
        if (col == currentCol && row == currentRow + direction) {
            // La case doit être vide
            if (!pieces.containsKey(col + "," + row)) {
                return true;  // Mouvement valide
            }
        }

        // Vérification du déplacement de deux cases pour le premier mouvement
        if (col == currentCol && row == currentRow + 2 * direction) {
            // Vérifier si le pion est sur sa ligne de départ
            if ((piece.getColor().equals("white") && currentRow == 1) || (piece.getColor().equals("black") && currentRow == 6)) {
                // Vérifier que la case intermédiaire est vide
                if (!pieces.containsKey(col + "," + (currentRow + direction))) {
                    // La case finale doit être vide
                    if (!pieces.containsKey(col + "," + row)) {
                        return true;  // Mouvement valide
                    }
                }
            }
        }

        // Vérification de l'attaque en diagonale (case diagonale occupée par une pièce adverse)
        if (Math.abs(col - currentCol) == 1 && row == currentRow + direction) {
            if (pieces.containsKey(col + "," + row)) {
                Piece targetPiece = pieces.get(col + "," + row);
                return !targetPiece.getColor().equals(piece.getColor()); // Attaque valide si la pièce adverse
            }
        }

        // Si aucune des conditions n'est remplie, le mouvement est invalide
        return false;
    }







    private boolean isRookMoveValid(Piece piece, int col, int row) {
        // Vérifier si la tour se déplace correctement et qu'il n'y a pas d'obstacle
        if (col == Integer.parseInt(selectedPosition.split(",")[0])) {
            // Mouvement vertical
            return !isVerticalPathBlocked(col, row);
        } else if (row == Integer.parseInt(selectedPosition.split(",")[1])) {
            // Mouvement horizontal
            return !isHorizontalPathBlocked(col, row);
        }
        return false;
    }

    private boolean isVerticalPathBlocked(int col, int row) {
        int start = Math.min(Integer.parseInt(selectedPosition.split(",")[1]), row) + 1;
        int end = Math.max(Integer.parseInt(selectedPosition.split(",")[1]), row);
        for (int r = start; r < end; r++) {
            if (pieces.containsKey(col + "," + r)) {
                return true;
            }
        }
        return false;
    }

    private boolean isHorizontalPathBlocked(int col, int row) {
        int start = Math.min(Integer.parseInt(selectedPosition.split(",")[0]), col) + 1;
        int end = Math.max(Integer.parseInt(selectedPosition.split(",")[0]), col);
        for (int c = start; c < end; c++) {
            if (pieces.containsKey(c + "," + row)) {
                return true;
            }
        }
        return false;
    }

    private boolean isKnightMoveValid(Piece piece, int col, int row) {
        // Le cavalier se déplace en "L", donc il peut sauter par-dessus les pièces
        int dx = Math.abs(col - Integer.parseInt(selectedPosition.split(",")[0]));
        int dy = Math.abs(row - Integer.parseInt(selectedPosition.split(",")[1]));
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    private boolean isBishopMoveValid(Piece piece, int col, int row) {
        // Vérifier le déplacement en diagonale
        if (Math.abs(col - Integer.parseInt(selectedPosition.split(",")[0])) == Math.abs(row - Integer.parseInt(selectedPosition.split(",")[1]))) {
            return !isDiagonalPathBlocked(piece, col, row);
        }
        return false;
    }

    private boolean isDiagonalPathBlocked(Piece piece, int col, int row) {
        int dx = col > Integer.parseInt(selectedPosition.split(",")[0]) ? 1 : -1;
        int dy = row > Integer.parseInt(selectedPosition.split(",")[1]) ? 1 : -1;
        int c = Integer.parseInt(selectedPosition.split(",")[0]) + dx;
        int r = Integer.parseInt(selectedPosition.split(",")[1]) + dy;
        while (c != col && r != row) {
            if (pieces.containsKey(c + "," + r)) {
                return true;
            }
            c += dx;
            r += dy;
        }
        return false;
    }

    private boolean isQueenMoveValid(Piece piece, int col, int row) {
        // La reine combine la tour et le fou
        return isRookMoveValid(piece, col, row) || isBishopMoveValid(piece, col, row);
    }

    private boolean isKingMoveValid(Piece piece, int col, int row) {
        // Le roi se déplace d'une case dans toutes les directions
        return Math.abs(col - Integer.parseInt(selectedPosition.split(",")[0])) <= 1 && Math.abs(row - Integer.parseInt(selectedPosition.split(",")[1])) <= 1;
    }

    private void movePiece(int col, int row) {
        String targetPosition = col + "," + row;

        // Vérifier si la case cible est occupée par une pièce adverse
        if (pieces.containsKey(targetPosition)) {
            // Retirer la pièce mangée de la carte 'pieces'
            pieces.remove(targetPosition);

            // Retirer la pièce de l'affichage sans toucher à la couleur de fond
            StackPane targetSquare = getSquareAt(col, row);
            targetSquare.getChildren().removeIf(node -> node instanceof Text); // Enlever la pièce, mais garder la couleur de fond
        }

        // Mettre à jour la carte des pièces avec la nouvelle position de la pièce
        pieces.put(targetPosition, selectedPiece);
        pieces.remove(selectedPosition);

        // Mettre à jour l'affichage
        StackPane oldSquare = getSquareAt(Integer.parseInt(selectedPosition.split(",")[0]), Integer.parseInt(selectedPosition.split(",")[1]));
        StackPane newSquare = getSquareAt(col, row);

        // Effacer la pièce de l'ancienne case, mais garder la couleur de fond
        oldSquare.getChildren().removeIf(node -> node instanceof Text); // Effacer la pièce de l'ancienne case sans changer la couleur de fond

        // Ajouter la pièce à la nouvelle case
        Text pieceSymbol = new Text(getSymbolForPiece(selectedPiece.getType(), selectedPiece.getColor()));
        pieceSymbol.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");
        newSquare.getChildren().add(pieceSymbol);
    }


    private void endGame(String message) {
        System.out.println(message);
        System.out.println("La partie est terminée.");

        // Bloquer toute autre interaction (par exemple, en vidant les pièces)
        pieces.clear();
        selectedPiece = null;

        // Si nécessaire, terminez le programme
        System.exit(0); // Si vous voulez quitter l'application complètement
    }



    private void highlightSelectedSquare(int col, int row) {
        StackPane square = getSquareAt(col, row);
        if (square != null) {
            square.setStyle("-fx-border-color: red; -fx-border-width: 2;");
        }
    }

    private void resetHighlighting() {
        // Réinitialiser les styles de surbrillance
        for (var node : chessBoard.getChildren()) {
            StackPane square = (StackPane) node;
            square.setStyle("");
        }
    }

    public Scene getScene() {
        return scene;
    }
    private String currentTurn = "white";  // Par défaut, les blancs commencent



}