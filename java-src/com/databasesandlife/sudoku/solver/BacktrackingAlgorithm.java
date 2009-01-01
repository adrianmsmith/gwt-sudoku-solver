package com.databasesandlife.sudoku.solver;

import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

/**
 * @author Adrian Smith &lt;adrian.m.smith@gmail.com&gt;
 */
public class BacktrackingAlgorithm extends SudokuSolver {
    
    protected static final class StackEntry {
        public final int offset9;
        public final int offset16;
        public final int x,y; // [0 .. 9)
        public final int[] possibilities = new int[10]; // p[number] == 0  =>  should be tried
        public StackEntry(int o9) { offset9 = o9; y = offset9 / 9; x = offset9 % 9; offset16 = y*16 + x; }
    }
    
    protected long maxMillis;
    protected long startTime;
    protected int[] board16 = new int[9 * 16];
    protected StackEntry[] stack;
    protected Result result;
    
    public BacktrackingAlgorithm(int maxSeconds) {
        maxMillis = maxSeconds * 1000;
    }
    
    protected void prepareStack() {
        Vector<StackEntry> stackVector = new Vector<StackEntry>(board16.length);
        for (int y = 0; y < 9; y++)
            for (int x = 0; x < 9; x++)
                if (board16[y*16 + x] == 0)
                    stackVector.add(new StackEntry(y*9 + x));
        stack = stackVector.toArray(new StackEntry[0]);
    }
    
    protected void setBoard16(int[] board9) {
        for (int y = 0; y < 9; y++) for (int x = 0; x < 9; x++) board16[y*16 + x] = board9[y*9 + x];
    }
    
    protected int[] getBoard9() {
        int[] board9 = new int[9 * 9];
        for (int y = 0; y < 9; y++) for (int x = 0; x < 9; x++) board9[y*9 + x] = board16[y*16 + x];
        return board9;
    }
            
    /** @param board 0 = unknown, 1..9 = value */
    @Override 
    public synchronized Result solve(int[] board9) {
//        assert 1 == 2;
//        if (true) throw new RuntimeException("assert doesn't work");
        
        assert board9.length == 9*9;
        
        result = new Result(Result.Type.ERR_NONE);
        startTime = new Date().getTime();
        setBoard16(board9);
        prepareStack();
        
        processNextSquare(0);
        
        return result;
    }
    
    /** @return to stop */
    protected boolean processNextSquare(final int stackEntryIdx) {
        // End? Then solution was found
        if (stackEntryIdx >= stack.length) {
            switch (result.type) {
                case ERR_NONE:                   result = new Result(Result.Type.UNIQUE, getBoard9());  return false;
                case UNIQUE: case WARN_MULTIPLE: result.type = Result.Type.WARN_MULTIPLE;               return true;
                default: throw new RuntimeException("result.type==" + result.type);
            }
        }
        
        // Timeout?
        if (stackEntryIdx < 50)
            if (new Date().getTime() - startTime > maxMillis) {
                result = new Result(Result.Type.ERR_TIMEOUT); return true;
            }
        
        final StackEntry entry = stack[stackEntryIdx];
        
        // Initially: all candidate 1..9 are possible
        Arrays.fill(entry.possibilities, 0);
        
        // Remove entries in row
        int oRowMax = (entry.offset16 & 0xF0) + 9;
        for (int oRow = entry.offset16 & 0xF0; oRow < oRowMax; oRow++) entry.possibilities[board16[oRow]] = 1;
        
        // Remove entries in column
        for (int oCol = entry.offset16 & 0x0F; oCol < board16.length; oCol += 16) entry.possibilities[board16[oCol]] = 1;
        
        // Remove entries in square
        int oSqMax = ((entry.y / 3) + 1) * (3 * 16);
        for (int oSq = (entry.y / 3) * (3 * 16) + (entry.x / 3) * 3; oSq < oSqMax; oSq += (16 - 3))
            for (int x = 0; x < 3; x++) entry.possibilities[board16[oSq++]] = 1;
        
        // Try all entries:
        for (int candidate = 1; candidate <= 9; candidate++) {
            if (entry.possibilities[candidate] == 0) {
                board16[entry.offset16] = candidate;
                if (processNextSquare(stackEntryIdx+1)) return true;
            }
        }
        board16[entry.offset16] = 0;

        return false;
    }
}
