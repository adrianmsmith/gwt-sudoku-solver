package adrian.soduku;

import java.util.*;

public class SodukuSolvingProcess {
    
    protected int[][] values; // [y][x], 1-9 for values and -1 for unknown
    protected boolean[][] isCellCalculated = new boolean[9][9];
    protected int durationMilliseconds;
    protected boolean isNonUniqueSolution = false;
    protected boolean isUnsolvable = false;
    
    public SodukuSolvingProcess(int[][] values) {
        this.values = values;
    }
    
    protected boolean isRowPossible(int y, int v) {
        for (int x = 0; x < 9; x++) 
            if (values[y][x] == v) return false;
        return true;
    }
    
    protected boolean isColumnPossible(int x, int v) {
        for (int y = 0; y < 9; y++) 
            if (values[y][x] == v) return false;
        return true;
    }
    
    protected boolean isSquarePossible(int x, int y, int v) {
        int minX = 3 * (x / 3);
        int minY = 3 * (y / 3);
        for (x = minX; x < minX+3; x++)
            for (y = minY; y < minY+3; y++)
                if (values[y][x] == v) return false;
        return true;
    }
    
    protected boolean isPossible(int x, int y, int v) {
        if ( ! isRowPossible(y, v)) return false;
        if ( ! isColumnPossible(x, v)) return false;
        if ( ! isSquarePossible(x, y, v)) return false;
        return true;
    }
    
    protected boolean isComplete() {
        for (int y = 0; y < 9; y++)
            for (int x = 0; x < 9; x++)
                if (values[y][x] == -1) return false;
        return true;
    }
    
    public void process() {
        Date start = new Date();
        
        while (true) {
            int resultCountThisIteration = 0;
            for (int x = 0; x < 9; x++)
                for (int y = 0; y < 9; y++)
                    if (values[y][x] == -1) {
                        int bestValYet = -1;
                        boolean multipleFound = false;
                        for (int v = 0; v < 9; v++) 
                            if (isPossible(x,y,v)) {
                                if (bestValYet != -1) multipleFound = true;
                                bestValYet = v;
                            }
                        if (bestValYet != -1 && !multipleFound) {
                            values[y][x] = bestValYet;
                            isCellCalculated[y][x] = true;
                            resultCountThisIteration++;
                        }
                        if (bestValYet == -1) {
                            isUnsolvable = true;
                            return;
                        }
                    }
            if (isComplete()) break;
            if (resultCountThisIteration == 0) {
                isNonUniqueSolution = true; break; }
        }
        
        durationMilliseconds = (int) (new Date().getTime() - start.getTime());
    }
    
    public long getDurationMilliseconds() { return durationMilliseconds; }
    public boolean isCellCalculated(int x, int y) { return isCellCalculated[y][x]; }
    public int getCellValue(int x, int y) { return values[y][x]; }
    public boolean isNonUniqueSolution() { return isNonUniqueSolution; }
    public boolean isUnsolvable() { return isUnsolvable; }
}