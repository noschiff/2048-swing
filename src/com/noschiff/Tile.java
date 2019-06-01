package com.noschiff;

import java.awt.*;

/**
 * @author noschiff
 *
 * Object to represent a single tile in the game.
 */
public class Tile {

    //colors from https://gist.github.com/hevertonfreitas/b7dd41cdba2be7c571ff0737ab7b296a
    private static final Color[] backgroundColors = {new Color(0xfff4d3), new Color(0xffdac3),
            new Color(0xe7b08e), new Color(0xe7bf8e), new Color(0xffc4c3), new Color(0xE7948e), new Color(0xbe7e56),
            new Color(0xbe5e56), new Color(0x9c3931), new Color(0x701710), new Color(0xEFC84F)};
    private static final Color[] textColors = {new Color(0x701710), new Color(0xFFE4C3)};

    private int value;
    private boolean merged;

    public Tile(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void doubleValue() {
        value *= 2;
    }

    public boolean wasMerged() {
        return merged;
    }

    public void setMerged(boolean merged) {
        this.merged = merged;
    }

    public Color textColor() {
        return (value < 128) ? textColors[0] : textColors[1];
    }

    public Color backgroundColor() {
        try {
            return backgroundColors[(int) (Math.log(value) / Math.log(2)) - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return Color.BLACK;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
