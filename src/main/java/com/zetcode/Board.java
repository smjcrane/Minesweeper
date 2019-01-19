package com.zetcode;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Board extends JPanel {

    public static final int NUM_IMAGES = 13;
    public static final int CELL_SIZE = 15;

    public static final int COVER_FOR_CELL = 10;
    public static final int MARK_FOR_CELL = 10;
    public static final int EMPTY_CELL = 0;
    public static final int MINE_CELL = 9;
    public static final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    public static final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;

    public static final int DRAW_MINE = 9;
    public static final int DRAW_COVER = 10;
    public static final int DRAW_MARK = 11;
    public static final int DRAW_WRONG_MARK = 12;

    public static final int N_MINES = 40;
    public static final int N_ROWS = 16;
    public static final int N_COLS = 16;

    public static final int BOARD_WIDTH = N_ROWS * CELL_SIZE + 1;
    public static final int BOARD_HEIGHT = N_COLS * CELL_SIZE + 1;

    private int[] field;
    private double[] probField;
    private boolean inGame;
    private int minesLeft;
    private Image[] img;

    private int allCells;
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

    private void newGame() {

        int cell;

        Random random = new Random();
        inGame = true;
        minesLeft = N_MINES;

        allCells = N_ROWS * N_COLS;
        field = new int[allCells];
        probField = new double[allCells];

        for (int i = 0; i < allCells; i++) {
            field[i] = COVER_FOR_CELL;
            probField[i] = ((double)i)/allCells;
        }

        statusbar.setText(Integer.toString(minesLeft));

        int i = 0;
        
        while (i < N_MINES) {

            int position = (int) (allCells * random.nextDouble());

            if ((position < allCells)
                    && (field[position] != COVERED_MINE_CELL)) {

                int current_col = position % N_COLS;
                field[position] = COVERED_MINE_CELL;
                i++;

                if (current_col > 0) {
                    cell = position - 1 - N_COLS;
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position - 1;
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }

                    cell = position + N_COLS - 1;
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                }

                cell = position - N_COLS;
                if (cell >= 0) {
                    if (field[cell] != COVERED_MINE_CELL) {
                        field[cell] += 1;
                    }
                }
                
                cell = position + N_COLS;
                if (cell < allCells) {
                    if (field[cell] != COVERED_MINE_CELL) {
                        field[cell] += 1;
                    }
                }

                if (current_col < (N_COLS - 1)) {
                    cell = position - N_COLS + 1;
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position + N_COLS + 1;
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position + 1;
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                }
            }
        }
    }

    public int[] getField(){
        return field;
    }

    public void find_empty_cells(int j) {

        int current_col = j % N_COLS;
        int cell;

        if (current_col > 0) {
            cell = j - N_COLS - 1;
            if (cell >= 0) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j - 1;
            if (cell >= 0) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + N_COLS - 1;
            if (cell < allCells) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }
        }

        cell = j - N_COLS;
        if (cell >= 0) {
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL) {
                    find_empty_cells(cell);
                }
            }
        }

        cell = j + N_COLS;
        if (cell < allCells) {
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL) {
                    find_empty_cells(cell);
                }
            }
        }

        if (current_col < (N_COLS - 1)) {
            cell = j - N_COLS + 1;
            if (cell >= 0) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + N_COLS + 1;
            if (cell < allCells) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + 1;
            if (cell < allCells) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }
        }

    }

    @Override
    public void paintComponent(Graphics g) {

        int uncover = 0;

        for (int i = 0; i < N_ROWS; i++) {
            for (int j = 0; j < N_COLS; j++) {

                int cell = field[(i * N_COLS) + j];

                if (inGame && cell == MINE_CELL) {
                    inGame = false;
                }

                if (!inGame) {
                    if (cell == COVERED_MINE_CELL) {
                        cell = DRAW_MINE;
                    } else if (cell == MARKED_MINE_CELL) {
                        cell = DRAW_MARK;
                    } else if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_WRONG_MARK;
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                    }

                } else {
                    if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_MARK;
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                        uncover++;
                    }
                }

                if (cell != COVER_FOR_CELL ) {
                    g.drawImage(img[cell], (j * CELL_SIZE),
                            (i * CELL_SIZE), this);
                }
                else {
                    double prob = probField[(i * N_COLS) + j]+0.05;
                    g.setColor(prob < 1 ? new Color( 255, (int) (255 * (1-prob)),(int) (255 * (1-prob))) : new Color(0,0,0));
                    g.fillRect(j* CELL_SIZE +1, i*CELL_SIZE + 1, CELL_SIZE-1, CELL_SIZE-1);
                    //g.drawImage(img[cell], (j * CELL_SIZE),
                     //       (i * CELL_SIZE), new Color(255,0,0), this);

                }
            }
        }

        if (uncover == 0 && inGame) {
            inGame = false;
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

        if ((cCol < N_COLS) && (cRow < N_ROWS)) {

            if (isFlag) {

                if (field[(cRow * N_COLS) + cCol] > MINE_CELL) {
                    doRepaint = true;

                    if (field[(cRow * N_COLS) + cCol] <= COVERED_MINE_CELL) {
                        if (minesLeft > 0) {
                            field[(cRow * N_COLS) + cCol] += MARK_FOR_CELL;
                            minesLeft--;
                            String msg = Integer.toString(minesLeft);
                            statusbar.setText(msg);
                        } else {
                            statusbar.setText("No marks left");
                        }
                    } else {

                        field[(cRow * N_COLS) + cCol] -= MARK_FOR_CELL;
                        minesLeft++;
                        String msg = Integer.toString(minesLeft);
                        statusbar.setText(msg);
                    }
                }

            } else {

                if (field[(cRow * N_COLS) + cCol] > COVERED_MINE_CELL) {
                    return false;
                }

                if ((field[(cRow * N_COLS) + cCol] > MINE_CELL)
                        && (field[(cRow * N_COLS) + cCol] < MARKED_MINE_CELL)) {

                    field[(cRow * N_COLS) + cCol] -= COVER_FOR_CELL;
                    doRepaint = true;

                    if (field[(cRow * N_COLS) + cCol] == MINE_CELL) {
                        inGame = false;
                    }

                    if (field[(cRow * N_COLS) + cCol] == EMPTY_CELL) {
                        find_empty_cells((cRow * N_COLS) + cCol);
                    }
                }
            }

            if (doRepaint) {
                repaint();
            }

        }
        return false;
    }
    private class MinesAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {

            int x = e.getX();
            int y = e.getY();

            int cCol = x / CELL_SIZE;
            int cRow = y / CELL_SIZE;

            pickCell(cCol, cRow, e.getButton() == MouseEvent.BUTTON3);
        }
    }
}

