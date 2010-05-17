package com.databasesandlife.sudoku.client;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Models the process of solving a single Sudoku.
 *
 * @author Adrian Smith &lt;adrian.m.smith@gmail.com&gt;
 */
public class SudokuSolver {

    public static final class Result {
        public enum Type {
                                        ERR_TIMEOUT,
                 /** No result found */ ERR_NONE,
          /** Multiple results found */ ERR_MULTIPLE,
                /** One result found */ UNIQUE
        };
        public Type type;
        public int[] board;
        public Result(Type t)           { type=t; board=null; }
        public Result(Type t, int[] b9) { type=t; board=copy(b9); }
    }

    protected class StackEntry {
        int offset; // 0 = top-left most square
        int[] incompatibleFutureOffsetsTimes16;
        int lastTriedCandidate = 0; // 0 = no candidate tried yet
    }

    int[] board;
    Result result = new Result(Result.Type.ERR_NONE);
    int[] illegalValueCount;  // offset*16 + (1..9) => How many times is this value illegal for this square
    StackEntry[] stack;
    List<StackEntry> stackVec = new Vector<StackEntry>(9*9);
    int stackEntryIdx;

    protected static int[] copy(int[] in) {
        int[] result = new int[in.length];
        for (int i = 0; i < in.length; i++) result[i] = in[i];
        return result;
    }

    protected int[] setToArray(Set<Integer> s, int multiplier) {
        int[] result = new int[s.size()];
        int i = 0;
        for (Integer value : s) result[i++] = value * multiplier;
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

    public SudokuSolver(int[] boardParam) {
        board = copy(boardParam);

        illegalValueCount = new int[9*9*16];
        Arrays.fill(illegalValueCount, 0);

        for (int y = 0; y < 9; y++)
            for (int x = 0; x < 9; x++) {
                int offset = y*9 + x;
                Set<Integer> incompatibleOffsets = incompatibleSquares(x, y);
                if (board[offset] == 0) {
                    removeNonFuture(incompatibleOffsets, offset);
                    StackEntry e = new StackEntry();
                    e.offset = offset;
                    e.incompatibleFutureOffsetsTimes16 = setToArray(incompatibleOffsets, 16);
                    stackVec.add(e);
                } else {
                    for (int incompatibleOffset : incompatibleOffsets) illegalValueCount[incompatibleOffset*16 + board[offset]] ++;
                }
            }

        stack = stackVec.toArray(new StackEntry[0]);
        stackEntryIdx = 0;
    }

    /**
     * Perform quanta of work torwards finding the solution to this Sudoku.
     * @param millis Stop performing work after approx this number of milliseconds has elapsed.
     * @return if the process is completed, return the result; otherwise return null meaning there is more work to be done.
     */
    public Result continueSolving(long millis) {
        long start = new Date().getTime();
        int itersUntilConsiderTimeout = 0;

        ITER: while (true) {

            // Don't check the time each iteration, "new Date().getTime()" is expensive
            if (itersUntilConsiderTimeout == 0) {
                itersUntilConsiderTimeout = 100;
                if ((new Date().getTime() - start) > millis) return null;
            }
            itersUntilConsiderTimeout--;

            // Reached the end of the Sudoku? Then solution was found
            if (stackEntryIdx >= stack.length) {
                switch (result.type) {
                    case ERR_NONE:
                        result = new Result(Result.Type.UNIQUE, board);
                        stackEntryIdx--;
                        continue ITER;
                    case UNIQUE:
                        result = new Result(Result.Type.ERR_MULTIPLE);
                        break ITER;
                    default:
                        throw new RuntimeException("result.type==" + result.type);
                }
            }

            // Before the start? Then no new solution can be found: Leave result as ERR_NONE or UNIQUE as set previously
            if (stackEntryIdx < 0)
                break ITER;

            StackEntry e = stack[stackEntryIdx];

            // If we tried a candidate for this square already, and it failed, remove "incompatibleOffset" records for that candidate
            if (e.lastTriedCandidate != 0) {
                final int candidate = e.lastTriedCandidate;
                for (int incompatibleOffset16 : e.incompatibleFutureOffsetsTimes16) illegalValueCount[incompatibleOffset16 + candidate] --;
            }

            // Find next candidate
            int nextCandidate = e.lastTriedCandidate;
            do nextCandidate++; while (nextCandidate <= 9 && illegalValueCount[e.offset*16 + nextCandidate] != 0);

            // A candidate found? Then try it
            if (nextCandidate <= 9) {
                board[e.offset] = nextCandidate;
                for (int incompatibleOffset16 : e.incompatibleFutureOffsetsTimes16) illegalValueCount[incompatibleOffset16 + nextCandidate] ++;
                e.lastTriedCandidate = nextCandidate;
                stackEntryIdx ++;
                continue ITER;
            }

            // Else: No more candidates, previous square should try its next candidate
            e.lastTriedCandidate = 0; // reset so that if we come to this level again, try all candidates again
            stackEntryIdx --;
        }

        return result;
    }

}
