package com.databasesandlife.sudoku.solver;

/**
 * @author Adrian Smith &lt;adrian.m.smith@gmail.com&gt;
 */
public class SudokuSolverUtil {
    
    public static int[] copy(int[] in) {
        int[] result = new int[in.length];
        for (int i = 0; i < in.length; i++) result[i] = in[i];
        return result;
    }
}
