package com.databasesandlife.sudoku.solver.cooperativethread;

import com.databasesandlife.sudoku.solver.SudokuSolver.Result;
import com.databasesandlife.sudoku.solver.SudokuSolverUtil;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * @author Adrian Smith &lt;adrian.m.smith@gmail.com&gt;
 */
public class BacktrackingProcess2 extends SudokuSolverProcess {

    protected class StackEntry {
        int offset; // 0 = top-left most square
        int[] incompatibleFutureOffsets;
        int lastTriedCandidate = 0; // 0 = no candidate tried yet
    }

    int[] illegalValueCount;  // offset*16 + (1..9) => How many times is this value illegal for this square
    StackEntry[] stack;
    int stackEntryIdx = 0;
    
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
    
    public BacktrackingProcess2(int[] board) {
        super(board);
        this.board = SudokuSolverUtil.copy(this.board);

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
        stackEntryIdx = 0;
    }

    public void processNextStep() {
        // End? Then solution was found
        if (stackEntryIdx >= stack.length) {
            switch (result.type) {
                case ERR_NONE:
                    result = new Result(Result.Type.UNIQUE, board);
                    stackEntryIdx--;
                    return;
                case UNIQUE:
                    result = new Result(Result.Type.WARN_MULTIPLE);
                    isFinished = true;
                    return;
                default:
                    throw new RuntimeException("result.type==" + result.type);
            }
        }
        
        // Before the start? Then no new solution can be found: Leave result as ERR_NONE or UNIQUE as set previously
        if (stackEntryIdx < 0) {
            isFinished = true;
            return;
        }
        
        StackEntry e = stack[stackEntryIdx];
        
        // If we tried a candidate for this square already, and it failed, remove "incompatibleOffset" records for that candidate
        if (e.lastTriedCandidate != 0) {
            final int candidate = e.lastTriedCandidate;
            for (int incompatibleOffset : e.incompatibleFutureOffsets) illegalValueCount[incompatibleOffset*16 + candidate] --;
        }
        
        // Find next candidate
        int nextCandidate = e.lastTriedCandidate;
        do nextCandidate++; while (nextCandidate <= 9 && illegalValueCount[e.offset*16 + nextCandidate] != 0);
        
        // A candidate found? Then try it
        if (nextCandidate <= 9) {
            board[e.offset] = nextCandidate;
            for (int incompatibleOffset : e.incompatibleFutureOffsets) illegalValueCount[incompatibleOffset*16 + nextCandidate] ++;
            e.lastTriedCandidate = nextCandidate;
            stackEntryIdx ++;
            return;
        }
        
        // Else: No more candidates, previous square should try its next candidate
        e.lastTriedCandidate = 0; // reset so that if we come to this level again, try all candidates again
        stackEntryIdx --;
    }
    
    // --------------------------------------------------------------------------------------------------------------------
    // For testing
    // --------------------------------------------------------------------------------------------------------------------
    
    public static class SudokuSolver extends com.databasesandlife.sudoku.solver.SudokuSolver {
        final protected int seconds;
        public SudokuSolver(int seconds) { this.seconds = seconds; }
        @Override public Result solve(int[] board) {
            SudokuSolverProcess p = new BacktrackingProcess2(board);
            Date start = new Date();
            while (!p.isFinished() && new Date().getTime() - start.getTime() < 1000*seconds) p.processNextStep();
            if (p.isFinished()) return p.getResult();
            else return new Result(Result.Type.ERR_TIMEOUT);
        }
    }
}
