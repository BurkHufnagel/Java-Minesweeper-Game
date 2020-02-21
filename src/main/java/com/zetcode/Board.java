package com.zetcode;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Board extends JPanel {
    protected final int NUM_IMAGES = 13;
    private final int CELL_SIZE = 15;

    private final int COVER_FOR_CELL = 10;  // All cells start "covered" meaning we don't know what's in it.Same value as MARK_FOR_CELL -- confusion?
    private final int MARK_FOR_CELL = 10;   // Added when a cell is flagged
    private final int EMPTY_CELL = 0;
    private final int MINE_CELL = 9;
    private final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    private final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;

    protected final int DRAW_MINE = 9;
    protected final int DRAW_COVER = 10;
    protected final int DRAW_MARK = 11;
    protected final int DRAW_WRONG_MARK = 12;

    private int numberOfRows = 16;
    private int numberOfColumns = 16;
    private int numberOfMines = 40;

    private Cell[] cells;

    private boolean inGame;
    private int minesLeft;
    protected Image[] images;

    // These are the offsets for a cell's row and column that identify its neighboring cells.
    protected final int[] COLUMN_AND_ROW_OFFSETS = new int[] {
            -1, -1,  -1, 0,  -1, 1, // Neighbors on the row above
             0, -1,           0, 1, // Neighbors on the same row
             1, -1,   1, 0,   1, 1  // Neighbors on the row below
    };

    private int allCells;           // Total number of cells on the board -- should be N_ROWS * N_COLS
    private final JLabel statusbar; // Tracks the number of flags left and let's you know if you won or lost the game


    public Board(JLabel statusbar) {
        this.statusbar = statusbar;
    }


    // Use default board size
    protected void initBoard() {
        initBoard(numberOfColumns, numberOfRows, numberOfMines);
    }


    // The 'board' is initialized before a game starts, so you might find a mine on your first move.
    protected void initBoard(int columns, int rows, int numberOfMines) {
        this.numberOfColumns = columns;
        this.numberOfRows = rows;
        this.numberOfMines = numberOfMines;

        int boardWidth = (columns * CELL_SIZE) + 1;
        int boardHeight = (rows * CELL_SIZE) + 1;
        setPreferredSize(new Dimension(boardWidth, boardHeight));

        images = loadImages();
        addMouseListener(new MinesAdapter());
        newGame(); // populates the board and resets the game logic
    }


    protected Image[] loadImages() {
        Image[] images = new Image[NUM_IMAGES];

        for (int i = 0; i < NUM_IMAGES; i++) {   // Load the images into memory
            var path = "src/resources/" + i + ".png";
            images[i] = (new ImageIcon(path)).getImage();
        }

        return images;
    }


    protected void newGame() {
        var random = new Random(23);                   // Init Random number generator
        inGame = true;                               // Mark the game as started
        minesLeft = numberOfMines;                   // Set the number of mines to place

        allCells = numberOfRows * numberOfColumns;   // Get total number of cells -- also used when checking fora cell's neighbors
        cells = new Cell[allCells];                  // Cells default to being covered so we don't need to set their state

        for (int i = 0; i < allCells; i++) {         // Set all the board cells as empty and covered
            cells[i] = new Cell();
        }

        statusbar.setText(Integer.toString(minesLeft));  // Update the status to show no mines are currently marked

        int minesLeft = numberOfMines;
        do {                                                        // Start planting mines
            int position = (int) (allCells * random.nextDouble());  // Pick a random location

            Cell cell = cells[position];
            if (cell.isNotMined()) {
                cell.plantMine();
                minesLeft--;                                        // Remember we planted the mine

                int[] neighbors = getNeighbors(position);           // Get the neighboring cell positions

                for(int index = 0; index < neighbors.length; index++) {
                    int neighbor = neighbors[index];
                    if (cells[neighbor].isNotMined()) {              // If the neighbor isn't a mined cell
                        cells[neighbor].incrementMinedNeighbors();   //    Increment it's number of mined neighbors.
                    }
                }
            }
        } while (minesLeft > 0); // Keep going while there's more mines left
    }


    // When a cell gets opened and it's empty, then open all the neighbors and if one of them is empty, repeat.
    // This cascade is how most of the board gets exposed.
    private void find_empty_cells(int position) {                  // position specifies an empty cell that just got opened, so open it's neighbors
        int[] neighbors = getNeighbors(position);                  // Get the neighboring cell positions

        for(int index = 0; index < neighbors.length; index++) {
            int neighbor = neighbors[index];
            if (cells[neighbor].isCovered()) {                    // If it's covered
                cells[neighbor].uncover();                        //   uncover it
                if (cells[neighbor].getMinedNeighbors() == 0) {   //   If it's empty, open it's neighbors
                    find_empty_cells(neighbor);
                }
            }
        }
    }


    protected int[] getNeighbors(int position) {
        int cellRow = position / numberOfColumns;
        int cellColumn = position % numberOfColumns;

        List<Integer> neighbors = new ArrayList<>();

        for (int i = 0; i < COLUMN_AND_ROW_OFFSETS.length; i++) {
            int newRow = cellRow + COLUMN_AND_ROW_OFFSETS[i++];
            int newColumn = cellColumn + COLUMN_AND_ROW_OFFSETS[i];

            if (newColumn >= 0 && newColumn < numberOfColumns &&  newRow >= 0 && newRow < numberOfRows) {
                neighbors.add( (newColumn) + (newRow  * numberOfColumns) );
            }
        }

        return neighbors.stream().mapToInt(Integer::intValue).toArray();
    }


    @Override
    public void paintComponent(Graphics g) {
        int unflaggedMines = 0;                                // Used to track the number of unflagged mines

        for (int i = 0; i < numberOfRows; i++) {               // For each row
            for (int j = 0; j < numberOfColumns; j++) {        //   For each column
                Cell cell = cells[(i * numberOfColumns) + j];  //     Get the cell value

                if (inGame && cell.isMined() && (cell.isUncovered())) {             // If the game isn't over and it's an exposed mine
                    inGame = false;                            //   the game is over
                }

                int imageIndex = cell.getMinedNeighbors();
                if (!inGame) {                                 // If the game is over
                    imageIndex = getImageIndexWhenGameIsOver(cell, imageIndex);
                } else {                                       // If the game is still going
                    if (cell.isFlagged() ) {                   //    and the cell is flagged
                        imageIndex = DRAW_MARK;                //        set it to display a flag
                    } else if (cell.isCovered()) {             //    and the cell is an unflagged mine
                        imageIndex = DRAW_COVER;               //         set it to display a covered cell
                        unflaggedMines++;                      //         increment the count of unflagged mines
                    }
                }

                g.drawImage(images[imageIndex], (j * CELL_SIZE), (i * CELL_SIZE), this);  // paint this cell on the board
            }
        }

        if (unflaggedMines == 0 && inGame) {         // If all the mines are marked and the game is not over
            inGame = false;                   //    Mark the game as over
            statusbar.setText("Game won");    //    Declare the player won the game
        } else if (!inGame) {                 // If the game is over
            statusbar.setText("Game lost");   //    Declare the player lost the game
        }
    }

    protected int getImageIndexWhenGameIsOver(Cell cell, int defaultImageIndex) {
        int imageIndex = defaultImageIndex;

        if (cell.isMined() && cell.isCovered() && cell.isNotFlagged()) {            //    and the cell is a covered mine
            imageIndex = DRAW_MINE;                          //        set it to display a mine
        } else if (cell.isMined() && cell.isFlagged()) {     //    and the cell is a flagged mine
            imageIndex = DRAW_MARK;                          //        set it to display a flag
        } else if (cell.isNotMined() && cell.isFlagged()) {  //    and the cell is incorrectly flagged
            imageIndex = DRAW_WRONG_MARK;                    //        set it to display an incorrect flag
        } else if (cell.isNotMined()) {                      //    and the value is > 9 ?? should have been handled by COVERED_MINE_CELL and MARKED_MINE_CELL
            imageIndex = DRAW_COVER;                         //        set it to display a covered cell
        }

        return imageIndex;
    }


    private class MinesAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            int x = e.getX();                      // Raw x value
            int y = e.getY();                      // Raw y value

            int cCol = x / CELL_SIZE;              // Selected column
            int cRow = y / CELL_SIZE;              // Selected row

            boolean doRepaint = false;             // Don't repaint the board unless something's changed

            if (!inGame) {                         // If game is over
                newGame();                         //    start a new game
                repaint();                         //    display the new board
            }

            if ((x < numberOfColumns * CELL_SIZE) && (y < numberOfRows * CELL_SIZE)) { // If click is on the grid
                Cell cell = cells[(cRow * numberOfColumns) + cCol];

                if (e.getButton() == MouseEvent.BUTTON3) {                       // On a right-button click

                    if (cell.isCovered()) {    //    If the cell is still covered (unknown value)
                        doRepaint = true;

                        if (cell.isNotFlagged()) { // If cell isn't flagged
                            if (minesLeft > 0) {                                           // If there are flags left
                                cell.toggleFlagged();   // Flag this cell
                                minesLeft--;                                               // Decrement the available flags
                                String msg = Integer.toString(minesLeft);                  // Update the status to show the number of flags left
                                statusbar.setText(msg);
                            } else {                                                      // If there aren't any flags left
                                statusbar.setText("No marks left");                       // Update status to say "No marks left"
                            }
                        } else {                                                          // If the cell is flagged
                            cell.toggleFlagged();      // Remove the flag
                            minesLeft++;                                                  // Increment the number of available flags
                            String msg = Integer.toString(minesLeft);                     // Update the status to show the number of flags left
                            statusbar.setText(msg);
                        }
                    }
                } else {                                                                  // If it's not a right click
                    if (cell.isFlagged()) {     // If it's flagged, ignore the click
                        return;
                    }

                    if ((cell.isCovered()) && (cell.isNotFlagged())) {        // If the cell is unopened and it isn't marked
                        cell.uncover();          // Uncover (open) it and let the board know it's dirty (repaint)
                        doRepaint = true;

                        if (cell.isMined()) {         // If the call contains a mine, mark the game as ended
                            inGame = false;
                        }

                        if (cell.isNotMined() && cell.getMinedNeighbors() == 0) {        // If the cell has no mines or neighbors with mines, open its neighbors. (Recursive call)
                            find_empty_cells((cRow * numberOfColumns) + cCol);
                        }
                    }
                }

                if (doRepaint) {  // Redraw the board if needed
                    repaint();
                }
            }
        }
    }
}
