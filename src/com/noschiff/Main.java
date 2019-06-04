package com.noschiff;

/**
 * Run the Swing application.
 */
public class Main {
    public static void main(String[] args) {
        Game2048 game = new Game2048(900, 700);
        game.setResizable(true);
        game.setVisible(true);
        game.requestGameFocus();
    }
}
