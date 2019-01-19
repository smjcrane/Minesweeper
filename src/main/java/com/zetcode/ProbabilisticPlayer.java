package com.zetcode;

import io.improbable.keanu.distributions.discrete.Bernoulli;
import io.improbable.keanu.tensor.dbl.DoubleTensor;

public class ProbabilisticPlayer {
    private Board board;
    private int i;

    public ProbabilisticPlayer(Board board){
        this.board = board;
        this.i = 0;
        initProbabilities();
    }

    private void initProbabilities(){
        double p = Board.N_MINES / (Board.N_COLS * Board.N_ROWS);
        Bernoulli b = Bernoulli.withParameters(DoubleTensor.create(p, new long[] {Board.N_ROWS, Board.N_COLS}));
    }

    public void play(){
        int cCol = 0; //TODO
        int cRow = 0; //TODO

        int[] cells = board.getField();
        //System.out.println(cells);

        board.pickCell(i, i, false);
        i++;
//        board.pickCell(cCol, cRow, false);
//        board.pickCell(10,10, false);
    }
}
