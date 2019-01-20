package com.zetcode;

import javax.swing.*;
import java.awt.*;
import io.improbable.keanu.*;


/**
 * Java Minesweeper Game
 * 
 * Author: Jan Bodnar
 * Website: http://zetcode.com
 */

public class Mines extends JFrame {

    private JLabel statusbar;
    
    public Mines() {
        initUI();
    }
    
    private void initUI() {

        statusbar = new JLabel("");
        add(statusbar, BorderLayout.SOUTH);

        Board board = new Board(statusbar);
        add(board);
        ProbabilisticPlayer player = new ProbabilisticPlayer(board);

        setResizable(false);
        pack();
        setVisible(true);
        setTitle("Minesweeper");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (board.getGamesPlayed() < 200) {
                    player.play();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        t.start();
    }
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Mines ex = new Mines();
            ex.setVisible(true);
        });
    }
}
