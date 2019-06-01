package com.noschiff;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Game2048 extends JFrame implements ActionListener {

    private JMenuBar mb_menu;
    private JMenu m_file, m_help;
    private JCheckBox m_sound;
    private JMenuItem mi_file_exit, mi_new_game;
    private JMenuItem mi_help_about;

    private GridBoard game;

    public Game2048(int width, int height) {
        // Setup swing specifics

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("2048");
        setSize(width, height);
        setLocationRelativeTo(null);

        // Setup menu
        mb_menu = new JMenuBar();
        setJMenuBar(mb_menu);
        m_file = new JMenu("File");
        mb_menu.add(m_file);
        m_help = new JMenu("Help");
        mb_menu.add(m_help);
        m_sound = new JCheckBox("Enable Sound");
        m_sound.setSelected(true);
        mb_menu.add(m_sound);
        mi_new_game = new JMenuItem("New Game");
        mi_new_game.addActionListener(this);
        mi_file_exit = new JMenuItem("Exit");
        mi_file_exit.addActionListener(this);
        m_file.add(mi_new_game);
        m_file.add(new JSeparator());
        m_file.add(mi_file_exit);
        mi_help_about = new JMenuItem("About");
        mi_help_about.addActionListener(this);
        m_help.add(mi_help_about);

        // Setup game board
        game = new GridBoard();
        add(game);
        game.requestFocusInWindow();
        m_sound.addActionListener(this);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                //force proportions
                Rectangle b = e.getComponent().getBounds();
                e.getComponent().setBounds(b.x, b.y, b.width, (int) (b.width * 700.0 / 900));
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource().equals(mi_file_exit)) {
            GameControl.quit();
        } else if (ae.getSource().equals(mi_new_game)) {
            GameControl.newGame(game);
        } else if (ae.getSource().equals(mi_help_about)) {
            GameControl.about();
        } else if (ae.getSource().equals(m_sound)) {
            game.enableSound(m_sound.isSelected());
            requestGameFocus();
        }
    }

    public void requestGameFocus() {
        game.requestFocus();
    }
}
