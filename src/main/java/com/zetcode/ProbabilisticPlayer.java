package com.zetcode;

import io.improbable.keanu.distributions.discrete.Bernoulli;
import io.improbable.keanu.tensor.Tensor;
import io.improbable.keanu.tensor.bool.BooleanTensor;
import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.VertexLabel;
import io.improbable.keanu.vertices.bool.BooleanVertex;
import io.improbable.keanu.vertices.bool.nonprobabilistic.operators.multiple.BooleanReduceVertex;
import io.improbable.keanu.vertices.bool.probabilistic.BernoulliVertex;
import io.improbable.keanu.vertices.dbl.KeanuRandom;
import io.improbable.keanu.vertices.intgr.IntegerVertex;
import io.improbable.keanu.vertices.intgr.nonprobabilistic.operators.IntegerModelResultVertex;
import io.improbable.keanu.vertices.intgr.probabilistic.UniformIntVertex;
import io.improbable.keanu.vertices.model.ModelVertex;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Map;
import java.util.Optional;

import static com.zetcode.Board.*;

public class ProbabilisticPlayer {
    private Board board;
    private int i;
    private double[][] probabilityIsBomb;

    public ProbabilisticPlayer(Board board){
        this.board = board;
        this.i = 0;
        initProbabilities();
    }

    private void initProbabilities(){
    }

    public void play(){
        double minProb = 1;
        int minRow = -1;
        int minCol = -1;
        double maxProb = 0;
        int maxRow = -1;
        int maxCol = -1;

        double[][] probs = board.getProbs();
        int[][] vis = board.getCellVisibilities();

        for (int i = 0; i < N_ROWS; i++){
            for (int j = 0; j < N_COLS; j ++) {
                if (vis[i][j] == INVISIBLE && probs[i][j] < minProb) {
                    minProb = probs[i][j];
                    minRow = i;
                    minCol = j;
                }
                if (vis[i][j] == INVISIBLE && probs[i][j] > maxProb) {
                    maxProb = probs[i][j];
                    maxRow = i;
                    maxCol = j;
                }
            }
        }
        if (minCol == -1 && maxCol == -1){
            return;
        }
        if (maxProb >= 0.99){
            board.pickCell(maxCol, maxRow, true);
        } else {
            board.pickCell(minCol, minRow, false);
        }
    }


}
