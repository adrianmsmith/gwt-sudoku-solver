package com.databasesandlife.sudoku.client.config;

/**
 * @author Adrian Smith
 */
public class SudokuConfig {

    /**
     * Speed tests on a difficult Sudoku http://z.about.com/d/puzzles/1/0/l/e/1/sudokux103.gif on my dad's  2.3GHz/core:
     *   - IE 8:     708ms
     *   - Opera 10: 114ms
     *   - Chrome 4:  49ms
     *   - FF 3.6:   334ms
     *   - Safari 4: 100ms
     */
    public int getTimeoutMillis() {
        return 1250;
    }
}
