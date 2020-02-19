package com.zetcode;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Java Minesweeper Game
 *
 * Author: Jan Bodnar
 * Website: http://zetcode.com
 */

public class Minesweeper extends JFrame {

    public Minesweeper() {
        initUI();
    }

    private void initUI() {
        JLabel statusbar = new JLabel("");
        add(statusbar, BorderLayout.SOUTH);

        Board board = new Board(statusbar);
        board.initBoard();
        add(board);

        setResizable(false);
        pack();

        setTitle("Minesweeper");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            var ex = new Minesweeper();
            ex.setVisible(true);
        });
    }
}
