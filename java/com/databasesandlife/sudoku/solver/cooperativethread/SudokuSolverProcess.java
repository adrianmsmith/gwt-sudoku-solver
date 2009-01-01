package com.databasesandlife.sudoku.solver.cooperativethread;

import com.databasesandlife.sudoku.solver.SudokuSolver;
import com.databasesandlife.sudoku.solver.SudokuSolver.Result;
import java.util.Date;

/**
 * Each object solves one Sudoku.
 * 
 * @author Adrian Smith &lt;adrian.m.smith@gmail.com&gt;
 */
abstract public class SudokuSolverProcess {
    
    protected Date startTime = new Date();
    protected int[] board;   // input
    protected boolean isFinished = false;
    protected Result result = new Result(Result.Type.ERR_NONE);
    
    public SudokuSolverProcess(int[] board) {
        this.board = board;
    }
    
    public boolean isFinished() { return isFinished; }
    public Result getResult() { if (!isFinished) throw new IllegalStateException(); return result; }
    
    abstract public void processNextStep();
}
