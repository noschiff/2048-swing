package com.noschiff;

import javax.swing.*;

public interface GameControl {

    public static void quit() {
        System.exit(0);
    }

    public static void newGame(GridBoard game) {
        JPanel options = new JPanel();
        SpinnerNumberModel modela = new SpinnerNumberModel(4, 1, 20, 1);
        SpinnerNumberModel modelb = new SpinnerNumberModel(4, 1, 20, 1);
        JSpinner rows = new JSpinner(modela);
        JSpinner columns = new JSpinner(modelb);
        options.add(new JLabel("Rows: "));
        options.add(rows);
        options.add(new JLabel("Columns: "));
        options.add(columns);
        JOptionPane.showMessageDialog(null, options, "New Game", JOptionPane.PLAIN_MESSAGE);
        game.reset((int) rows.getValue(), (int) columns.getValue());
        game.requestFocus();
    }

    public static void about() {
        JOptionPane.showMessageDialog(null, "2048 is a single-player sliding block puzzle game.\nThe game's objective is to slide numbered tiles on a grid\n to combine them to create a tile with the number 2048.\nThis Java Swing implementation was created by Noah S.", "About", JOptionPane.PLAIN_MESSAGE, GridBoard.popUpIcon);
    }

    public static void win(GridBoard game) {
        int response = JOptionPane.showOptionDialog(null, "Congratulations, you have reached the " + game.getMax() + " tile!\nWhat would you like to do now?", "You Won!", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, GridBoard.popUpIcon, new String[]{"Quit", "New Game", "Continue Playing"}, 0);
        switch (response) {
            case 0:
                quit();
                break;
            case 1:
                newGame(game);
                break;
            case 2:
                break;
        }
    }

    public static void lost(GridBoard game) {
        int response = JOptionPane.showOptionDialog(null, "You lost!\nWhat would you like to do now?", "You Lost!", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, GridBoard.popUpIcon, new String[]{"Quit", "New Game"}, 0);
        switch (response) {
            case 0:
                quit();
                break;
            case 1:
                newGame(game);
                break;
        }
    }
}
