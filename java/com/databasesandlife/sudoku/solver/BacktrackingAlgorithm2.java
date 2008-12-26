package com.databasesandlife.sudoku.solver;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * @author Adrian Smith &lt;adrian.m.smith@gmail.com&gt;
 */
public class BacktrackingAlgorithm2 extends SudokuSolver {
    
    protected class StackEntry {
        /** 0 = top-left most square */ int offset;
        int[] incompatibleFutureOffsets;
    }

    protected long maxMillis;
    protected long startTime;
    int[] board;              // offset => value
    int[] illegalValueCount;  // offset*16 + (1..9) => How many times is this value illegal for this square
    StackEntry[] stack;
    Result result;

    public BacktrackingAlgorithm2(int maxSeconds) {
        maxMillis = maxSeconds * 1000;
    }
    
    protected int[] setToArray(Set<Integer> s) {
        int[] result = new int[s.size()];
        int i = 0;
        for (Integer value : s) result[i++] = value;
        assert i == s.size();
        return result;
    }
    
    protected Set<Integer> incompatibleSquares(int x, int y) {
        HashSet<Integer> result = new HashSet<Integer>(25);
        for (int i = 0; i < 9; i++) result.add(y*9 + i);
        for (int i = 0; i < 9; i++) result.add(i*9 + x);
        for (int ix = x-x%3; ix < x-x%3+3; ix++)
            for (int iy = y-y%3; iy < y-y%3+3; iy++)
                result.add(iy*9 + ix);
        return result;
    }
    
    protected void removeNonFuture(Set<Integer> s, int offset) {
        for (Iterator<Integer> i = s.iterator(); i.hasNext(); ) 
            if (i.next() <= offset) i.remove();
    }
    
    @Override
    public synchronized Result solve(int board[]) {
        startTime = new Date().getTime();
        this.board = Arrays.copyOf(board, board.length);
        this.result = new Result(Result.Type.ERR_NONE);
        
        illegalValueCount = new int[9*9*16];
        Arrays.fill(illegalValueCount, 0);
        
        Vector<StackEntry> stackVec = new Vector<StackEntry>(9*9);
        for (int y = 0; y < 9; y++)
            for (int x = 0; x < 9; x++) {
                int offset = y*9 + x;
                Set<Integer> incompatibleOffsets = incompatibleSquares(x, y);
                if (board[offset] == 0) {
                    removeNonFuture(incompatibleOffsets, offset);
                    StackEntry e = new StackEntry();
                    e.offset = offset;
                    e.incompatibleFutureOffsets = setToArray(incompatibleOffsets);
                    stackVec.add(e);
                } else {
                    for (int incompatibleOffset : incompatibleOffsets) illegalValueCount[incompatibleOffset*16 + board[offset]] ++;
                }
            }
        stack = stackVec.toArray(new StackEntry[0]);

        processNextSquare(0);
        
        return result;
    }
    
    /** @return stop processing? */
    public boolean processNextSquare(int stackEntryIdx) {
        // End? Then solution was found
        if (stackEntryIdx >= stack.length) {
            switch (result.type) {
                case ERR_NONE:                    result = new Result(Result.Type.UNIQUE, board);  return false;
                case UNIQUE: case WARN_MULTIPLE:  result = new Result(Result.Type.WARN_MULTIPLE);  return true;
                default: throw new RuntimeException("result.type==" + result.type);
            }
        }
        
        // Timeout?
        if (stackEntryIdx < 50)
            if (new Date().getTime() - startTime > maxMillis) {
                result = new Result(Result.Type.ERR_TIMEOUT); return true;
            }

        StackEntry e = stack[stackEntryIdx];
        
        for (int candidate = 1; candidate <= 9; candidate++) {
            if (illegalValueCount[e.offset*16 + candidate] == 0) {
                board[e.offset] = candidate;
                for (int incompatibleOffset : e.incompatibleFutureOffsets) illegalValueCount[incompatibleOffset*16 + candidate] ++;
                if (processNextSquare(stackEntryIdx + 1)) return true;
                for (int incompatibleOffset : e.incompatibleFutureOffsets) illegalValueCount[incompatibleOffset*16 + candidate] --;
            }
        }
        
        return false;
    }
}
