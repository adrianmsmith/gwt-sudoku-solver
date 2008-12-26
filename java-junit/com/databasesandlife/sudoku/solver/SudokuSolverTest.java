package com.databasesandlife.sudoku.solver;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import junit.framework.TestCase;

public class SudokuSolverTest extends TestCase {
    
    public SudokuSolverTest(String testName) {
        super(testName);
    }
    
    protected void assertEquals(int[] expected, int[] actual) {
        for (int y = 0; y < 9; y++)
            for (int x = 0; x < 9; x++)
                assertEquals("x="+x+", y="+y, expected[y*9+x], actual[y*9+x]);
    }
    
    protected <T extends SudokuSolver> void time(int[] board, Class<T> cl) {
        try {
            Date start = null;
            Date end;
            int iterations = -1;
            do {
                cl.getConstructor(int.class).newInstance(2).solve(board);
                end = new Date();
                if (start == null) start = new Date(); // ignore first iteration
                iterations ++ ;
            } while (end.getTime() < start.getTime() + 2000);
            System.out.println("Class " + cl.getName() + " took " +
                    String.format("%.4f", ((double) end.getTime() - start.getTime()) / 1000 / iterations) + " seconds per iter " +
                    "(" + iterations + " iterations)");
        }
        catch (NoSuchMethodException e) { throw new RuntimeException(e); }
        catch (InstantiationException e) { throw new RuntimeException(e); }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
        catch (InvocationTargetException e) { throw new RuntimeException(e); }
    }

    public void testSolve() {
        int[] board = new int[] {
            0, 0, 0,  6, 0, 4,  0, 5, 7,
            0, 0, 7,  0, 1, 0,  8, 0, 0,
            5, 0, 0,  0, 7, 0,  0, 0, 6,
            
            9, 0, 0,  0, 0, 0,  0, 2, 1, 
            0, 0, 0,  2, 0, 3,  0, 0, 0,
            4, 7, 0,  0, 0, 0,  0, 0, 8,
            
            7, 0, 0,  0, 5, 0,  0, 0, 4,
            0, 0, 6,  0, 4, 0,  9, 0, 0,
            1, 4, 0,  3, 0, 9,  0, 0, 0
        };
        int[] board2 = new int[] {
            0, 0, 0,  6, 0, 4,  0, 5, 7,
            0, 0, 7,  0, 1, 0,  8, 0, 0,
            5, 0, 0,  0, 7, 0,  0, 0, 6,
            
            9, 0, 0,  0, 0, 0,  0, 2, 1, 
            0, 0, 0,  2, 0, 3,  0, 0, 0,
            4, 7, 0,  0, 0, 0,  0, 0, 8,
            
            7, 0, 0,  0, 5, 0,  0, 0, 4,
            0, 0, 0,  0, 0, 0,  0, 0, 0,
            1, 4, 0,  3, 0, 9,  0, 0, 0
        };
        int[] result = new int[] {
            8, 9, 1,  6, 3, 4,  2, 5, 7,
            2, 6, 7,  9, 1, 5,  8, 4, 3, 
            5, 3, 4,  8, 7, 2,  1, 9, 6,
            
            9, 8, 3,  4, 6, 7,  5, 2, 1,
            6, 1, 5,  2, 8, 3,  4, 7, 9,
            4, 7, 2,  5, 9, 1,  6, 3, 8, 
            
            7, 2, 9,  1, 5, 6,  3, 8, 4,
            3, 5, 6,  7, 4, 8,  9, 1, 2, 
            1, 4, 8,  3, 2, 9,  7, 6, 5
        };
        
        SudokuSolver.Result r = new BacktrackingAlgorithm(2).solve(board);
        assertEquals(SudokuSolver.Result.Type.UNIQUE, r.type);
        assertEquals(result, r.board);
        
        SudokuSolver.Result r2 = new BacktrackingAlgorithm2(2).solve(board);
        assertEquals(SudokuSolver.Result.Type.UNIQUE, r2.type);
        assertEquals(result, r2.board);
        
        time(board, BacktrackingAlgorithm.class);
        time(board, BacktrackingAlgorithm2.class);
    }
}
