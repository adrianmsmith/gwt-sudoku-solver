package com.databasesandlife.sudoku.solver;

import java.util.Arrays;

/**
 * @author Adrian Smith &lt;adrian.m.smith@gmail.com&gt;
 */
abstract public class SudokuSolver {

    public static final class Result {
        public enum Type {
                                        ERR_TIMEOUT,
                 /** No result found */ ERR_NONE,
          /** Multiple results found */ WARN_MULTIPLE,
                /** One result found */ UNIQUE
        
        };
        public Type type;
        public int[] board;
        public Result(Type t)           { type=t; board=null; }
        public Result(Type t, int[] b9) { type=t; board=Arrays.copyOf(b9, b9.length); }
    }
    
    abstract public Result solve(int board[]);
}
