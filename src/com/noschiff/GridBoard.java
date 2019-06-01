package com.noschiff;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Random;

public class GridBoard extends JPanel {

    //Mechanics
    private Tile[][] grid;
    private int max;
    private int score;
    private boolean sound;
    private double fillPercent;
    private boolean won, lost;

    //Resources
    private static final File move = new File("resources/move.wav");
    private static final File merge = new File("resources/merge.wav");
    public static final ImageIcon logo = new ImageIcon("resources/logo.png");
    public static final ImageIcon popUpIcon = new ImageIcon(logo.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));

    //Util
    private static final Random rand = new Random();

    public GridBoard(int rows, int columns, double fillPercent, int max) {

        this.grid = new Tile[rows][columns];
        this.fillPercent = fillPercent;
        fillBoard(fillPercent);
        //this.winCondition = (max == -1) ? true : false;
        this.max = max;
        score = 0;
        sound = true;
        won = false;
        lost = false;

        setPreferredSize(new Dimension(900, 700));
        setBackground(new Color(0xFAF8EF));
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        slide(Direction.UP);
                        break;
                    case KeyEvent.VK_DOWN:
                        slide(Direction.DOWN);
                        break;
                    case KeyEvent.VK_LEFT:
                        slide(Direction.LEFT);
                        break;
                    case KeyEvent.VK_RIGHT:
                        slide(Direction.RIGHT);
                        break;
                    case KeyEvent.VK_L:
                        addTile(0, 0, max);
                        break;
                    case KeyEvent.VK_A:
                        GameControl.quit();
                }
                repaint();
            }
        });
    }

    public GridBoard(int rows, int columns) {
        this(rows, columns, 12.5, 2048);

    }

    public GridBoard() {
        this(4, 4, 12.5, 2048);
    }

    public void reset(int rows, int columns, double fillPercent) {
        this.grid = new Tile[rows][columns];
        fillBoard(fillPercent);
        won = false;
        lost = false;
        score = 0;
        repaint();
    }

    public void reset(int rows, int columns) {
        reset(rows, columns, this.fillPercent);
    }

    public void reset() {
        reset(grid.length, grid[0].length);
    }

    public int getMax() {
        return max;
    }

    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g);

        if (!won && win()) {
            won = true;
            GameControl.win(this);
        }
        if (!lost && !movesRemaining()) {
            lost = true;
            GameControl.lost(this);
        }
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(new Color(0xBBADA0));
        Dimension windowSize = getParent().getBounds().getSize();
        int x = (int) (windowSize.getWidth() * 200 / 900);
        int y = (int) (windowSize.getHeight() * 100 / 700);
        int width = (int) (windowSize.getWidth() * 499 / 900);
        int height = (int) (windowSize.getHeight() * 499 / 700);
        int cornerOffset = (int) (windowSize.getWidth() * 15 / 900);

        g.fillRoundRect(x, y, width + cornerOffset, height + cornerOffset, 15, 15);
		
        /*if (!full()) {
		g.setColor(new Color(0xFFEBCD));
		g.fillRoundRect(x, y, width + 15, height + 15, 15, 15);

        g.setColor(new Color(0xBBADA0).darker());
        g.setFont(new Font("SansSerif", Font.BOLD, 128));
        g.drawString("2048", (int)(windowSize.getWidth()*310/900), (int) (windowSize.getHeight()*270/700));

        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString("game over", (int)(windowSize.getWidth()*400/900), (int) (windowSize.getHeight()*350/700));
        }
         else { */
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                drawTile(g, r, c, x, y, width, height, cornerOffset);
            }
        }
//        //}
//        if (win()) {
//            g.setFont(new Font("SansSerif", Font.BOLD, (int) (width * height * 500.0 / 630000)));
//            g.setColor(Color.RED);
//            String s = "WINNER";
//            FontMetrics fm = g.getFontMetrics();
//            int asc = fm.getAscent();
//            int dec = fm.getDescent();
//            int sx = (windowSize.width - fm.stringWidth(s)) / 2;
//            int sy = (asc + (windowSize.height - (asc + dec)) / 2);
//            g.drawString(s, sx, sy);
//        }
        g.setFont(new Font("SansSerif", Font.BOLD, 50));
        FontMetrics fm = g.getFontMetrics();
        String s = "Score: " + score;
        int sx = (windowSize.width - fm.stringWidth(s));
        int sy = fm.getHeight();
        g.setColor(Color.BLUE);
        g.drawString(s, sx, fm.getHeight());
    }

    private void drawTile(Graphics2D g, int r, int c, int xOffset, int yOffset, int gridWidth, int gridHeight, int cornerOffset) {

        int widthY = (int) (gridHeight * .876 / grid.length);
        int widthX = (int) (gridWidth * .876 / grid[0].length);
        int intervalY = (int) ((double) gridHeight / grid.length);
        int intervalX = (int) ((double) gridWidth / grid[0].length);

        Tile tile = grid[r][c];

        if (tile != null) {

            g.setColor(tile.backgroundColor());
            g.fillRoundRect(xOffset + cornerOffset + c * intervalX, yOffset + cornerOffset + r * intervalY, widthX, widthY, 7, 7);

            String s = String.valueOf(tile.getValue());
            g.setColor(tile.textColor());

            int fontSize = (int) (.0043609826 * widthX * widthY);
            setFont(new Font("SansSerif", Font.BOLD, fontSize < 10 ? 10 : fontSize));
            FontMetrics fm = g.getFontMetrics();
            int asc = fm.getAscent();
            int dec = fm.getDescent();
            int x = xOffset + cornerOffset + c * intervalX + (widthX - fm.stringWidth(s)) / 2;
            int y = yOffset + cornerOffset + r * intervalY + (asc + (widthY - (asc + dec)) / 2);

            g.drawString(s, x, y);

        } else {
            g.setColor(new Color(0xCDC1B4));
            g.fillRoundRect(xOffset + cornerOffset + c * intervalX, yOffset + cornerOffset + r * intervalY, widthX, widthY, 7, 7);
        }
    }

    private void fillBoard(double percent) {
        int numberToFill = (int) (grid.length * grid[0].length * percent / 100);
        if (numberToFill == 0) {
            numberToFill++;
        }
        for (int i = 0; i < numberToFill; i++) {
            addRandomTile();
        }
    }

    private void addRandomTile() {

        int randRow, randCol, randValue;
        do {
            randRow = rand.nextInt(grid.length);
            randCol = rand.nextInt(grid[0].length);
        } while (grid[randRow][randCol] != null);

        randValue = (rand.nextInt(10) == 0) ? 4 : 2;
        addTile(randRow, randCol, randValue);
    }

    private void addTile(int row, int column, int value) {
        grid[row][column] = new Tile(value);
    }

    private void removeTile(int row, int column) {
        grid[row][column] = null;
    }

    private void merge(int row, int column, int delRow, int delCol) {
        removeTile(delRow, delCol);
        grid[row][column].doubleValue();
        grid[row][column].setMerged(true);
        score += grid[row][column].getValue();
    }

    private boolean canMerge(Tile t1, Tile t2) {
        if (t2 == null || t1 == null) {
            return false;
        }
        return t1.getValue() == t2.getValue();
    }

    public void slide(Direction dir) {
        slide(dir, true);
    }

    public boolean slide(Direction dir, boolean execute) {
        // resets for next slide
        for (Tile[] row : grid) {
            for (Tile tile : row) {
                if (tile != null) {
                    tile.setMerged(false);
                }
            }
        }
        boolean moved = false, merged = false;

        switch (dir) {
            case LEFT:
                for (int row = 0; row < grid.length; row++) {
                    for (int column = 0; column < grid[row].length; column++) {

                        Tile currentTile = grid[row][column];

                        if (column != 0 && currentTile != null) {

                            int nextC = column - 1;
                            while (grid[row][nextC] == null && nextC != 0) {
                                nextC--;
                            }
                            Tile nextTile = grid[row][nextC];

                            if (canMerge(currentTile, nextTile) && nextTile.wasMerged() == false) {
                                if (execute) merge(row, nextC, row, column);
                                moved = true;
                                merged = true;
                            } else {
                                int value = currentTile.getValue();
                                if (execute) removeTile(row, column);

                                if (nextTile == null) {
                                    if (execute) grid[row][nextC] = new Tile(value);
                                    moved = true;
                                } else {
                                    if (execute) grid[row][nextC + 1] = new Tile(value);
                                    if (nextC + 1 != column) {
                                        moved = true;
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case RIGHT:
                for (int row = 0; row < grid.length; row++) {
                    for (int column = grid[row].length - 1; column >= 0; column--) {

                        Tile currentTile = grid[row][column];

                        if (column != grid[row].length - 1 && currentTile != null) {
                            int nextC = column + 1;
                            while (grid[row][nextC] == null && nextC != grid[row].length - 1) {
                                nextC++;
                            }
                            Tile nextTile = grid[row][nextC];

                            if (canMerge(currentTile, nextTile) && nextTile.wasMerged() == false) {
                                if (execute) merge(row, nextC, row, column);
                                moved = true;
                                merged = true;
                            } else {
                                int value = currentTile.getValue();
                                if (execute) removeTile(row, column);

                                if (nextTile == null) {
                                    if (execute) grid[row][nextC] = new Tile(value);
                                    moved = true;
                                } else {
                                    if (execute) grid[row][nextC - 1] = new Tile(value);
                                    if (nextC - 1 != column) {
                                        moved = true;
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case UP:
                for (int row = 0; row < grid.length; row++) {
                    for (int column = 0; column < grid[row].length; column++) {

                        Tile currentTile = grid[row][column];

                        if (row != 0 && currentTile != null) {

                            int nextR = row - 1;
                            while (grid[nextR][column] == null && nextR != 0) {
                                nextR--;
                            }
                            Tile nextTile = grid[nextR][column];

                            if (canMerge(currentTile, nextTile) && nextTile.wasMerged() == false) {
                                if (execute) merge(nextR, column, row, column);
                                moved = true;
                                merged = true;
                            } else {
                                int value = currentTile.getValue();
                                if (execute) removeTile(row, column);

                                if (nextTile == null) {
                                    if (execute) grid[nextR][column] = new Tile(value);
                                    moved = true;
                                } else {
                                    if (execute) grid[nextR + 1][column] = new Tile(value);
                                    if (nextR + 1 != row) {
                                        moved = true;
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case DOWN:
                for (int row = grid.length - 1; row >= 0; row--) {
                    for (int column = 0; column < grid[row].length; column++) {

                        Tile currentTile = grid[row][column];

                        if (row != grid.length - 1 && currentTile != null) {

                            int nextR = row + 1;
                            while (grid[nextR][column] == null && nextR != grid.length - 1) {
                                nextR++;
                            }
                            Tile nextTile = grid[nextR][column];

                            if (canMerge(currentTile, nextTile) && nextTile.wasMerged() == false) {
                                if (execute) merge(nextR, column, row, column);
                                moved = true;
                                merged = true;
                            } else {
                                int value = currentTile.getValue();
                                if (execute) removeTile(row, column);

                                if (nextTile == null) {
                                    if (execute) grid[nextR][column] = new Tile(value);
                                    moved = true;
                                } else {
                                    if (execute) grid[nextR - 1][column] = new Tile(value);
                                    if (nextR - 1 != row) {
                                        moved = true;
                                    }
                                }
                            }
                        }
                    }
                }
                break;
        }
        if (moved && execute) {
            if (sound) {
                if (merged) {
                    playSound(merge);
                } else {
                    playSound(move);
                }
            }
            addRandomTile();
        }
        return moved;
    }

    public boolean gameOver() {
        return win() || full();
    }

    private boolean win() {
        for (Tile[] row : grid) {
            for (Tile tile : row) {
                if (tile != null && tile.getValue() >= max) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean full() {
        for (Tile[] row : grid) {
            for (Tile tile : row) {
                if (tile == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean movesRemaining() {
        for (Direction dir : Direction.values()) {
            if (slide(dir, false)) {
                return true;
            }
        }
        return false;
    }

    private void playSound(File soundFile) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
        } catch (Exception ignored) {
        }
    }

    public void enableSound(boolean selected) {
        sound = selected;
    }

    @Override
    public String toString() {
        return Arrays.deepToString(grid).replace("], ", "\n").replace(",", "").replace("[", "").replace("null", "0")
                .replace("]", "");
    }

}

