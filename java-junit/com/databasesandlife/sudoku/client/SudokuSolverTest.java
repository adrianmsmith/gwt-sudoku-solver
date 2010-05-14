package com.databasesandlife.sudoku.client;

import java.lang.reflect.InvocationTargetException;
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
    
    /** @return number of seconds to perform calculation */
    protected <T extends SudokuSolver> double time(int[] board, Class<T> cl) {
        try {
            long start = 0;
            long end;
            int iterations = -1;
            do {
                cl.getConstructor(int.class).newInstance(1).solve(board);
                end = System.nanoTime();
                if (start == 0) start = System.nanoTime();
                iterations ++ ;
            } while (end < start + 1000000000);
            return ((double) end - start) / 1000000000 / iterations;
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
        
        SudokuSolver.Result r = new SudokuSolver().setTimeoutInMilliseconds(2000).solve(board);
        assertEquals(SudokuSolver.Result.Type.UNIQUE, r.type);
        assertEquals(result, r.board);
    }
 }
