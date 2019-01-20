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

    public static final int neighbours[][] = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};

    public static final int BOARD_WIDTH = N_COLS * CELL_DISPLAY_SIZE + 1;
    public static final int BOARD_HEIGHT = N_ROWS * CELL_DISPLAY_SIZE + 1;

    private int[][] cellValues;
    private int[][] cellVisibilities;

    private double[][] probabilities;
    private int invisibleCount;
    private int[][] flaggedNeighboursCount;
    private int[][] probableFlags;
    private int[][] invisibleNeighboursCount;

    private int numMoves;

    private boolean inGame;
    private int minesLeft;
    private Image[] img;

    private final JLabel statusbar;

    private int gamesWon;
    private int gamesLost;
    private int gamesLostQuickly;

    private Random random;

    public Board(JLabel statusbar) {
        this.statusbar = statusbar;

        random = new Random();
        random.setSeed(42);

        initBoard();
        gamesWon = 0;
        gamesLost = 0;
        gamesLostQuickly = 0;

        flaggedNeighboursCount = new int[N_ROWS][N_COLS];
        probableFlags = new int[N_ROWS][N_COLS];
        invisibleNeighboursCount = new int[N_ROWS][N_COLS];
    }

    public int getGamesWon(){
        return gamesWon;
    }

    public int getGamesLost(){
        return gamesLost;
    }

    public int getGamesPlayed(){
        return gamesWon + gamesLost;
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

    public void newGame() {
        inGame = true;
        minesLeft = N_MINES;

        numMoves = 0;

        cellValues = new int[N_ROWS][N_COLS];
        cellVisibilities = new int[N_ROWS][N_COLS];
        probabilities = new double[N_ROWS][N_COLS];

        for (int i = 0; i < N_ROWS; i++) {
            for (int j = 0; j < N_COLS; j++){
                cellVisibilities[i][j] = INVISIBLE;
                cellValues[i][j] = EMPTY_CELL;
                probabilities[i][j] = ((double) N_MINES) / N_CELLS;
            }
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

                if (drawValue != DRAW_HIDDEN ) {
                    g.drawImage(img[drawValue], (j * CELL_DISPLAY_SIZE),
                            (i * CELL_DISPLAY_SIZE), this);
                }
                else {
                    double prob = probabilities[i][j];
                    g.setColor(prob < 1 ? new Color( 255, (int) (255 * (1-prob)),(int) (255 * (1-prob))) : new Color(0,0,0));
                    g.fillRect(j* CELL_DISPLAY_SIZE +1, i* CELL_DISPLAY_SIZE + 1, CELL_DISPLAY_SIZE -1, CELL_DISPLAY_SIZE -1);
                }
            }
        }

        if (minesLeft == 0){
            statusbar.setText("Game won");
            gamesWon ++;
            System.out.println("Games won: " + gamesWon + ", games lost: " + gamesLost + " of which quickly: " + gamesLostQuickly);
            System.out.println("That game was won in " + numMoves+" moves" );
            newGame();
        } else if (!inGame) {
            statusbar.setText("Game lost");
            gamesLost ++;
            System.out.println("Games won: " + gamesWon + ", games lost: " + gamesLost + " of which quickly: " + gamesLostQuickly);
            System.out.println("That game was lost in " + numMoves+" moves");
            if (numMoves < 5){
                gamesLostQuickly++;
            }
            newGame();
        }
    }

    public boolean pickCell(int cCol, int cRow, boolean isFlag) {
        if (!inGame) {
            return false;
        }

        int cellValue = cellValues[cRow][cCol];
        int cellVisibility = cellVisibilities[cRow][cCol];

        if (cellVisibility == VISIBLE){
            return false;
        }

        numMoves++;

        if (isFlag) {
            if (cellVisibility == FLAGGED){
                cellVisibilities[cRow][cCol] = INVISIBLE;
                minesLeft++;
            } else if (cellVisibility == INVISIBLE){
                cellVisibilities[cRow][cCol] = FLAGGED;
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
        updateProbabilities();
        return true;
    }

    private void updateProbabilities() {
        invisibleCount = 0;
        for (int i = 0; i < N_ROWS; i++) {
            for (int j = 0; j < N_COLS; j++) {
                invisibleNeighboursCount[i][j] = 0;
                flaggedNeighboursCount[i][j] = 0;
                probableFlags[i][j] = 0;
            }
        }
        for (int i = 0; i < N_ROWS; i++) {
            for (int j = 0; j < N_COLS; j++) {
                if (cellVisibilities[i][j] == INVISIBLE) {
                    invisibleCount++;
                    for (int[] neighbour : neighbours) {
                        int x = neighbour[0] + i;
                        int y = neighbour[1] + j;
                        if (x < 0 || x >= N_ROWS || y < 0 || y >= N_COLS) {
                            continue;
                        }
                        invisibleNeighboursCount[x][y]++;
                    }
                } else if (cellVisibilities[i][j] == FLAGGED) {
                    for (int[] neighbour : neighbours) {
                        int x = neighbour[0] + i;
                        int y = neighbour[1] + j;
                        if (x < 0 || x >= N_ROWS || y < 0 || y >= N_COLS) {
                            continue;
                        }
                        flaggedNeighboursCount[x][y]++;
                    }
                }
            }
        }
        for (int i = 0; i < N_ROWS; i++) {
            for (int j = 0; j < N_COLS; j++) {
                if (cellVisibilities[i][j] == INVISIBLE) {
                    probabilities[i][j] = calculateProbability(i, j);
                    for (int[] neighbour : neighbours) {
                        int x = neighbour[0] + i;
                        int y = neighbour[1] + j;
                        if (x < 0 || x >= N_ROWS || y < 0 || y >= N_COLS) {
                            continue;
                        }
                        probableFlags[x][y] += (probabilities[i][j] > 0.8) ? 1 : 0;
                    }
                }
            }
        }
        doBayes();
    }

    private void doBayes(){
        double newProbs[][] = new double[N_ROWS][N_COLS];
        for (int i = 0; i < N_ROWS; i++) {
            for (int j = 0; j < N_COLS; j++) {
                if (cellVisibilities[i][j] == INVISIBLE) {
                    if (probabilities[i][j] == 0 || probabilities[i][j] == 1 || invisibleNeighboursCount[i][j] == countNeighbours(i,j)){
                        newProbs[i][j] = probabilities[i][j];
                        continue;
                    }
                    double minfac = 1;
                    double maxfac = 0;
                    for (int[] neighbour : Board.neighbours) {
                        int x = i + neighbour[0];
                        int y = j + neighbour[1];
                        if (x < 0 || x >= N_ROWS || y < 0 || y >= N_COLS) {
                            continue;
                        }
                        double minpab = 1;
                        double maxpab = 0;
                        if (cellVisibilities[x][y] == INVISIBLE) {
                            for (int[] neighbour2 : Board.neighbours) {
                                int r = x + neighbour2[0];
                                int s = y + neighbour2[1];
                                if (r < 0 || r >= N_ROWS || s < 0 || s >= N_COLS) {
                                    continue;
                                }
                                if (r == i && s == j){
                                    continue;
                                }
                                if (cellVisibilities[r][s] == VISIBLE){
                                    if ((Math.abs(i - r) <= 1 && Math.abs(j - s) <= 1)){
                                        double pabn = ((double) cellValues[i][j] - flaggedNeighboursCount[i][j] - 1) / invisibleNeighboursCount[i][j];
                                        minpab = Math.min(minpab, pabn);
                                        maxpab = Math.max(maxpab, pabn);
                                    }
                                }
                            }
                            minfac = Math.min(minfac, minpab / probabilities[i][j]);
                            maxfac = Math.max(maxfac, maxpab / probabilities[i][j]);
                        }
                    }
                    if (minfac * probabilities[i][j] < 1 - maxfac * probabilities[i][j]){
                        newProbs[i][j] = Math.max(0,minfac * probabilities[i][j]);
                    } else {
                        newProbs[i][j] = Math.min(1,maxfac * probabilities[i][j]);
                    }
                }
            }
        }
        for (int i = 0; i < N_ROWS; i++) {
            for (int j = 0; j < N_COLS; j++){
                probabilities[i][j] = newProbs[i][j];
            }
        }
    }

    private double calculateProbability(int x, int y){
        if (invisibleNeighboursCount[x][y] == countNeighbours(x,y)){
            return ((double) minesLeft) / invisibleCount;
        }
        double minp = 1;
        double maxp = 0;
        for (int[] neighbour : Board.neighbours){
            int i = x + neighbour[0];
            int j = y + neighbour[1];
            if (i < 0 || i >= N_ROWS || j < 0 || j >= N_COLS){
                continue;
            }
            if (cellVisibilities[i][j] == VISIBLE){
                double p = ((double) cellValues[i][j] - flaggedNeighboursCount[i][j]) / invisibleNeighboursCount[i][j];
                minp = Math.min(p, minp);
                maxp = Math.max(p, maxp);
            }
        }
        if (minp < 1 - maxp){
            return minp;
        }
        return maxp;
    }

    private double calculateProbabilityBetter(int x, int y){
        if (invisibleNeighboursCount[x][y] == countNeighbours(x,y)){
            return ((double) minesLeft) / invisibleCount;
        }
        double minp = 1;
        double maxp = 0;
        for (int[] neighbour : Board.neighbours){
            int i = x + neighbour[0];
            int j = y + neighbour[1];
            if (i < 0 || i >= N_ROWS || j < 0 || j >= N_COLS){
                continue;
            }
            if (cellVisibilities[i][j] == VISIBLE){
                double p = ((double) cellValues[i][j] - flaggedNeighboursCount[i][j] - probableFlags[i][j]) / invisibleNeighboursCount[i][j];
                minp = Math.max(0,Math.min(p, minp));
                maxp = Math.min(1,Math.max(p, maxp));
            }
        }
        if (minp < 1 - maxp){
            return minp;
        }
        return maxp;
    }

    private int countNeighbours(int x, int y){
        if (x == 0 || x == N_ROWS - 1) {
            if (y == 0 || y == N_COLS - 1){
                return 3;
            }
            return 5;
        }
        if (y == 0 || y == N_COLS - 1 ){
            return 5;
        }
        return 8;
    }


    public double[][] getProbs(){
        return probabilities;
    }

    public int[][] getCellVisibilities(){
        return cellVisibilities;
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

