package com.zetcode;

public class Cell {
    private boolean isMined = false;
    private int minedNeighbors;
    private boolean isCovered = true; // All cells start covered
    private boolean isFlagged = false;

    public Cell() {
    }


    public boolean isMined() {
        return isMined;
    }


    public boolean isNotMined() {
        return !isMined;
    }


    public void plantMine() {
        isMined = true;
    }


    public boolean isCovered() {
        return isCovered;
    }

    public boolean isUncovered() {
        return !isCovered;
    }


    public void uncover() {
        isCovered = false;
    }


    public boolean isFlagged() {
        return isFlagged;
    }


    public boolean isNotFlagged() {
        return !isFlagged;
    }


    public void toggleFlagged() {
        isFlagged = !isFlagged;
    }


    public void incrementMinedNeighbors() {
        minedNeighbors++;
    }


    public int getMinedNeighbors() {
        return minedNeighbors;
    }
}
