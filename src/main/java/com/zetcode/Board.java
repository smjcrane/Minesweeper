package com.zetcode;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Board extends JPanel {

    public static final int NUM_IMAGES = 13;
    public static final int CELL_DISPLAY_SIZE = 15;
    
    public static final int EMPTY_CELL = 0;
    //CELL VALUE CAN ALSO BE 1 - 8
    public static final int MINE_CELL = 9;

    public static final int VISIBLE = 20;
    public static final int INVISIBLE = 21;
    public static final int FLAGGED = 22;

    //can also draw 0 to 8
    public static final int DRAW_MINE = 9;
    public static final int DRAW_HIDDEN = 10;
    public static final int DRAW_FLAG = 11;
    public static final int DRAW_WRONG_FLAG = 12;

    public static final int N_MINES = 40;
    public static final int N_ROWS = 16;
    public static final int N_COLS = 16;
    public static final int N_CELLS = N_ROWS * N_COLS;

    private static final int neighbours[][] = {{-1,0},{0,-1},{1,0},{0,1}};

    public static final int BOARD_WIDTH = N_ROWS * CELL_DISPLAY_SIZE + 1;
    public static final int BOARD_HEIGHT = N_COLS * CELL_DISPLAY_SIZE + 1;

    private int[][] cellValues;
    private int[][] cellVisibilities;
    private double[] probcellValues;
    private boolean inGame;
    private int minesLeft;
    private Image[] img;

    private final JLabel statusbar;

    public Board(JLabel statusbar) {
        this.statusbar = statusbar;
        initBoard();
    }

    private void initBoard() {

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        img = new Image[NUM_IMAGES];

        for (int i = 0; i < NUM_IMAGES; i++) {
            String path = "/src/main/resources/" + i + ".png";
            File file = new File(System.getProperty("user.dir").toString() + path);
            img[i] = (new ImageIcon(file.getAbsolutePath())).getImage();
        }

        addMouseListener(new MinesAdapter());
        newGame();
    }

    private void increment(int x, int y){
        if (x < 0){
            return;
        }
        if (y < 0){
            return;
        }
        if (x >= N_ROWS){
            return;
        }
        if (y >= N_COLS){
            return;
        }
        if (cellValues[x][y] == MINE_CELL){
            return;
        }
        cellValues[x][y]++;
    }

    private void placeMines(){
        Random random = new Random();

        for (int i = 0; i < N_MINES; i++){
            int x = random.nextInt(N_ROWS);
            int y = random.nextInt(N_COLS);

            while(cellValues[x][y] == MINE_CELL) {
                x = random.nextInt(N_ROWS);
                y = random.nextInt(N_COLS);
            }
            cellValues[x][y] = MINE_CELL;
            for (int r = -1; r <= 1; r++){
                for (int s = -1; s <= 1; s ++){
                    increment(x + r, y + s);
                }
            }
        }
    }

    private void newGame() {
        inGame = true;
        minesLeft = N_MINES;

        cellValues = new int[N_ROWS][N_COLS];
        cellVisibilities = new int[N_ROWS][N_COLS];
        probcellValues = new double[N_CELLS];

        for (int i = 0; i < N_ROWS; i++) {
            for (int j = 0; j < N_COLS; j++){
                cellVisibilities[i][j] = INVISIBLE;
                cellValues[i][j] = EMPTY_CELL;
            }
            probcellValues[i] = ((double)i)/N_CELLS;
        }

        statusbar.setText(Integer.toString(minesLeft));

        placeMines();
    }



    public void find_empty_cells(int x, int y) {

        if (cellValues[x][y] != EMPTY_CELL){
            cellVisibilities[x][y] = VISIBLE;
            return;
        }

        List<String> toVisit = new ArrayList<>();
        toVisit.add(x + " " + y);



        while (toVisit.size() > 0){
            String string = toVisit.get(0);
            int p = Integer.parseInt(string.substring(0, string.indexOf(" ")));
            int q = Integer.parseInt(string.substring(string.indexOf( " " )+ 1));
            for (int[] neighbour : neighbours){
                int r = neighbour[0];
                int s = neighbour[1];
                int rowPos = p + r;
                int colPos = q + s;
                if (rowPos < 0){
                    continue;
                }
                if (rowPos >= N_ROWS){
                    continue;
                }
                if (colPos < 0){
                    continue;
                }
                if (colPos >= N_COLS){
                    continue;
                }
                if (cellVisibilities[rowPos][colPos] == VISIBLE){
                    continue;
                }
                if (cellVisibilities[rowPos][colPos] == FLAGGED){
                    continue;
                }
                if (cellVisibilities[rowPos][colPos] == INVISIBLE){
                    cellVisibilities[rowPos][colPos] = VISIBLE;
                }
                if (cellValues[rowPos][colPos] == EMPTY_CELL){
                    toVisit.add(rowPos + " " + colPos);
                }
            }
            cellVisibilities[p][q] = VISIBLE;
            toVisit.remove(p + " " + q);
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        for (int i = 0; i < N_ROWS; i++) {
            for (int j = 0; j < N_COLS; j++) {

                int cellValue = cellValues[i][j];
                int cellVisibility = cellVisibilities[i][j];

                int drawValue = DRAW_HIDDEN;

                if (!inGame) {
                    if (cellValue == MINE_CELL) {
                        drawValue = DRAW_MINE;
                    } else if (cellVisibility == VISIBLE) {
                        drawValue = cellValue;
                    } else if (cellVisibility == INVISIBLE) {
                        drawValue = DRAW_HIDDEN;
                    } else if (cellVisibility == FLAGGED) {
                        drawValue = DRAW_WRONG_FLAG;
                    }

                } else {
                    if (cellVisibility == VISIBLE) {
                        drawValue = cellValue;
                    } else if (cellVisibility == INVISIBLE) {
                        drawValue = DRAW_HIDDEN;
                    } else if (cellVisibility == FLAGGED) {
                        drawValue = DRAW_FLAG;
                    }
                }

                if (cellVisibility != INVISIBLE ) {
                    g.drawImage(img[drawValue], (j * CELL_DISPLAY_SIZE),
                            (i * CELL_DISPLAY_SIZE), this);
                }
                else {
                    double prob = probcellValues[(i * N_COLS) + j]+0.05;
                    g.setColor(prob < 1 ? new Color( 255, (int) (255 * (1-prob)),(int) (255 * (1-prob))) : new Color(0,0,0));
                    g.fillRect(j* CELL_DISPLAY_SIZE +1, i* CELL_DISPLAY_SIZE + 1, CELL_DISPLAY_SIZE -1, CELL_DISPLAY_SIZE -1);
                }
            }
        }

        if (minesLeft == 0){
            statusbar.setText("Game won");
        } else if (!inGame) {
            statusbar.setText("Game lost");
        }
    }

    public boolean pickCell(int cCol, int cRow, boolean isFlag) {
        boolean doRepaint = false;

        if (!inGame) {
            newGame();
            repaint();
        }

        int cellValue = cellValues[cRow][cCol];
        int cellVisibility = cellVisibilities[cRow][cCol];

        if (cellVisibility == VISIBLE){
            return false;
        }

        if (isFlag) {
            if (cellVisibility == FLAGGED){
                cellVisibilities[cRow][cCol] = INVISIBLE;
                minesLeft++;
            } else {
                if (minesLeft > 0) {
                    minesLeft--;

                } else {
                    statusbar.setText("No marks left");
                    return false;
                }
            }
            String msg = Integer.toString(minesLeft);
            statusbar.setText(msg);
        } else {
            cellVisibilities[cRow][cCol] = VISIBLE;
            if (cellValue == MINE_CELL){
                inGame = false;
            } else {
                find_empty_cells(cRow, cCol);
            }
        }
        repaint();
        return true;
    }
    private class MinesAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {

            int x = e.getX();
            int y = e.getY();

            int cCol = x / CELL_DISPLAY_SIZE;
            int cRow = y / CELL_DISPLAY_SIZE;

            pickCell(cCol, cRow, e.getButton() == MouseEvent.BUTTON3);
        }
    }
}

