package com.devops.chess.models;

public class Piece {
    private String type; // Exemple : "pawn", "king", "queen", etc.
    private String color; // Exemple : "white" ou "black"
    private int col; // Colonne actuelle
    private int row; // Ligne actuelle

    // Constructeur principal
    public Piece(String type, String color, int col, int row) {
        this.type = type;
        this.color = color;
        this.col = col;
        this.row = row;
    }

    // Constructeur alternatif (par défaut)
    public Piece(String type, String color) {
        this.type = type;
        this.color = color;
        this.col = 0; // Valeur par défaut
        this.row = 0; // Valeur par défaut
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public void setPosition(int col, int row) {
        this.col = col;
        this.row = row;
    }
}
