package com.databasesandlife.sudoku.client.config;

/**
 * Medium speed
 *
 * @author Adrian Smith
 */
public class SudokuConfigFastish extends SudokuConfig {

    public int getTimeoutMillis() {
        return 500;
    }
}
