package com.zetcode;

public class Cell {
    private final int column;
    private final int row;
    private boolean hasMine;
    private int minedNeighbors;
    private boolean isCovered = true; // All cells start covered

    public Cell(int column, int row, boolean hasMine) {
        this.column = column;
        this.row = row;
        this.hasMine = hasMine;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isCovered() {
        return isCovered;
    }

    public void uncover() {
        isCovered = false;
    }

    public boolean hasMine() {
        return hasMine;
    }

    public void incrementMinedNeighbors() {
        minedNeighbors++;
    }

    public int getMinedNeighbors() {
        return minedNeighbors;
    }
}
