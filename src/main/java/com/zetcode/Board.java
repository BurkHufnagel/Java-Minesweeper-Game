package com.zetcode;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Board extends JPanel {

    private final int NUM_IMAGES = 13;
    private final int CELL_SIZE = 15;

    // Each value in the 'field' array represents one cell.
    // The value stored in a cell is a combination of several things.
    // Each cell is initialized with COVER_FOR_CELLL


    private final int COVER_FOR_CELL = 10;  // All cells start "covered" meaning we don't know what's in it.Same value as MARK_FOR_CELL -- confusion?
    private final int MARK_FOR_CELL = 10;   // Added when a cell is flagged
    private final int EMPTY_CELL = 0;
    private final int MINE_CELL = 9;
    private final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    private final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;

    private final int DRAW_MINE = 9;
    private final int DRAW_COVER = 10;
    private final int DRAW_MARK = 11;
    private final int DRAW_WRONG_MARK = 12;

    private final int N_MINES = 40;
    private final int N_ROWS = 16;
    private final int N_COLS = 16;

    private final int BOARD_WIDTH = N_COLS * CELL_SIZE + 1;
    private final int BOARD_HEIGHT = N_ROWS * CELL_SIZE + 1;

    private int[] field;
    private boolean inGame;
    private int minesLeft;
    private Image[] img;

    private int allCells;           // Total number of cells on the board -- should be N_ROWS * N_COLS
    private final JLabel statusbar; // Tracks the number of flags left and let's you know if you won or lost the game


    public Board(JLabel statusbar) {
        this.statusbar = statusbar;
        initBoard();
    }


    // The 'board' is initialized before a game starts, so you might find a mine on your first move.
    private void initBoard() {
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        img = new Image[NUM_IMAGES];  // Holds images used to draw the current board state

        for (int i = 0; i < NUM_IMAGES; i++) {   // Load the images into memory
            var path = "src/resources/" + i + ".png";
            img[i] = (new ImageIcon(path)).getImage();
        }

        addMouseListener(new MinesAdapter());
        newGame(); // populates the board and resets the game logic
    }


    private void newGame() {
        int cell;

        var random = new Random();           // Init Random number generator
        inGame = true;                       // Mark the game as started
        minesLeft = N_MINES;                 // Set the number of mines to place

        allCells = N_ROWS * N_COLS;          // Get total number of cells -- also used when checking fora cell's neighbors
        field = new int[allCells];           // contains all the cells on the board

        for (int i = 0; i < allCells; i++) {  // Set all the board cells as empty and covered
            field[i] = COVER_FOR_CELL;
        }

        statusbar.setText(Integer.toString(minesLeft));  // Update the status to show no mines are currently marked

        int i = 0;

        while (i < N_MINES) {                                       // Start planting mines
            int position = (int) (allCells * random.nextDouble());  // Pick a random location

            if (field[position] != COVERED_MINE_CELL) {             // If it doesn't already contain a mine
                int current_col = position % N_COLS;                //    Figure out which column it's in
                field[position] = COVERED_MINE_CELL;                //    Set the cell to an unflagged mine
                i++;                                                //    Increment the number of mines set

                if (current_col > 0) {                              // If the cell isn't on the left edge
                    cell = position - 1 - N_COLS;                   //    Get the upper left neighbor
                    if (cell >= 0) {                                //    If it's on the board
                        if (field[cell] != COVERED_MINE_CELL) {     //       and it doesn't contain a mine
                            field[cell] += 1;                       //       increment the number of mined neighbors
                        }
                    }

                    cell = position - 1;                         //    Get the upper left neighbor
                    if (cell >= 0) {                             //    If it's on the board
                        if (field[cell] != COVERED_MINE_CELL) {  //       and it doesn't contain a mine
                            field[cell] += 1;                    //       increment the number of mined neighbors
                        }
                    }

                    cell = position + N_COLS - 1;                //    Get the upper left neighbor
                    if (cell < allCells) {                       //    If it's on the board
                        if (field[cell] != COVERED_MINE_CELL) {  //       and it doesn't contain a mine
                            field[cell] += 1;                    //       increment the number of mined neighbors
                        }
                    }
                }

                cell = position - N_COLS;                    //    Get the upper left neighbor
                if (cell >= 0) {                             //    If it's on the board
                    if (field[cell] != COVERED_MINE_CELL) {  //       and it doesn't contain a mine
                        field[cell] += 1;                    //       increment the number of mined neighbors
                    }
                }

                cell = position + N_COLS;                    //    Get the upper left neighbor
                if (cell < allCells) {                       //    If it's on the board
                    if (field[cell] != COVERED_MINE_CELL) {  //       and it doesn't contain a mine
                        field[cell] += 1;                    //       increment the number of mined neighbors
                    }
                }

                if (current_col < (N_COLS - 1)) {                // If the cell isn't on the right edge
                    cell = position - N_COLS + 1;                //    Get the upper left neighbor
                    if (cell >= 0) {                             //    If it's on the board
                        if (field[cell] != COVERED_MINE_CELL) {  //       and it doesn't contain a mine
                            field[cell] += 1;                    //       increment the number of mined neighbors
                        }
                    }

                    cell = position + N_COLS + 1;                //    Get the upper left neighbor
                    if (cell < allCells) {                       //    If it's on the board
                        if (field[cell] != COVERED_MINE_CELL) {  //       and it doesn't contain a mine
                            field[cell] += 1;                    //       increment the number of mined neighbors
                        }
                    }

                    cell = position + 1;                         //    Get the upper left neighbor
                    if (cell < allCells) {                       //    If it's on the board
                        if (field[cell] != COVERED_MINE_CELL) {  //       and it doesn't contain a mine
                            field[cell] += 1;                    //       increment the number of mined neighbors
                        }
                    }
                }
            }
        }
    }

    // When a cell gets opened and it's empty, open all the neighbors and if one of them is empty, repeat.
    // This is how most of the board gets exposed.
    private void find_empty_cells(int j) {  // j is an empty cell that just got opened so open it's neighbors
        int current_col = j % N_COLS;
        int cell;

        if (current_col > 0) {                         // If the cell isn't on the left edge of the board
            cell = j - N_COLS - 1;                     // Get the cell to the upper left
            if (cell >= 0) {                           // If it's not off the board
                if (field[cell] > MINE_CELL) {         // If it's covered
                    field[cell] -= COVER_FOR_CELL;     //   uncover it
                    if (field[cell] == EMPTY_CELL) {   //   If it's empty, open it's neighbors
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j - 1;                             // Get the cell to the left
            if (cell >= 0) {                          // If it's not off the board
                if (field[cell] > MINE_CELL) {        // If it's covered
                    field[cell] -= COVER_FOR_CELL;    //   uncover it
                    if (field[cell] == EMPTY_CELL) {  //   If it's empty, open it's neighbors
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + N_COLS - 1;                    // Get the cell to the lower left
            if (cell < allCells) {                    // If it's not off the board
                if (field[cell] > MINE_CELL) {        // If it's covered
                    field[cell] -= COVER_FOR_CELL;    //   uncover it
                    if (field[cell] == EMPTY_CELL) {  //   If it's empty, open it's neighbors
                        find_empty_cells(cell);
                    }
                }
            }
        }

        cell = j - N_COLS;                        // Get the cell directly above
        if (cell >= 0) {                          // If it's not off the board
            if (field[cell] > MINE_CELL) {        // If it's covered
                field[cell] -= COVER_FOR_CELL;    //   uncover it
                if (field[cell] == EMPTY_CELL) {  //   If it's empty, open it's neighbors
                    find_empty_cells(cell);
                }
            }
        }

        cell = j + N_COLS;                         // Get the cell directly below
        if (cell < allCells) {                     // If it's not off the board
            if (field[cell] > MINE_CELL) {         // If it's covered
                field[cell] -= COVER_FOR_CELL;     //   uncover it
                if (field[cell] == EMPTY_CELL) {   //   If it's empty, open it's neighbors
                    find_empty_cells(cell);
                }
            }
        }

        if (current_col < (N_COLS - 1)) {             // If the cell isn't on the right edge of the board
            cell = j - N_COLS + 1;                    // Get the upper right cell
            if (cell >= 0) {                          // If it's not off the board
                if (field[cell] > MINE_CELL) {        // If it's covered
                    field[cell] -= COVER_FOR_CELL;    //   uncover it
                    if (field[cell] == EMPTY_CELL) {  //   If it's empty, open it's neighbors
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + N_COLS + 1;                     // Get the lower right cell
            if (cell < allCells) {                     // If it's not off the board
                if (field[cell] > MINE_CELL) {         // If it's covered
                    field[cell] -= COVER_FOR_CELL;     //   uncover it
                    if (field[cell] == EMPTY_CELL) {   //   If it's empty, open it's neighbors
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + 1;                              // Get the cell to the right
            if (cell < allCells) {                     // If it's not off the board
                if (field[cell] > MINE_CELL) {         // If it's covered
                    field[cell] -= COVER_FOR_CELL;     //   uncover it
                    if (field[cell] == EMPTY_CELL) {   //   If it's empty, open it's neighbors
                        find_empty_cells(cell);
                    }
                }
            }
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        int uncover = 0;                          // Used to track the number of unflagged mines

        for (int i = 0; i < N_ROWS; i++) {            // For each row
            for (int j = 0; j < N_COLS; j++) {        // For each column
                int cell = field[(i * N_COLS) + j];   // Get the cell value

                if (inGame && cell == MINE_CELL) {     // If the game isn't over and it's an exposed mine
                    inGame = false;                    //   the game is over
                }

                if (!inGame) {                             // If the game is over
                    if (cell == COVERED_MINE_CELL) {       //    and the cell is a covered mine
                        cell = DRAW_MINE;                  //        set it to display a mine
                    } else if (cell == MARKED_MINE_CELL) { //    and the cell is a flagged mine
                        cell = DRAW_MARK;                  //        set it to display a flag
                    } else if (cell > COVERED_MINE_CELL) { //    and the cell is incorrectly flagged
                        cell = DRAW_WRONG_MARK;            //        set it to display an incorrect flag
                    } else if (cell > MINE_CELL) {         //    and the value is > 9 ?? should have been handled by COVERED_MINE_CELL and MARKED_MINE_CELL
                        cell = DRAW_COVER;                 //        set it to display a covered cell
                    }
                } else {                                   // If the game is still going
                    if (cell > COVERED_MINE_CELL) {        //    and the cell is a flagged mine
                        cell = DRAW_MARK;                  //        set it to display a flag
                    } else if (cell > MINE_CELL) {         //    and the cell is an unflagged mine
                        cell = DRAW_COVER;                 //         set it to display a covered cell
                        uncover++;                         //         increment the count of unflagged mines
                    }
                }

                g.drawImage(img[cell], (j * CELL_SIZE), (i * CELL_SIZE), this);  // paint this cell on the board
            }
        }

        if (uncover == 0 && inGame) {       // If all the mines are marked and the game is not over
            inGame = false;                 //    Mark the game as over
            statusbar.setText("Game won");  //    Declare the player won the game
        } else if (!inGame) {               // If the game is over
            statusbar.setText("Game lost"); //    Declare the player lost the game
        }
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

            if ((x < N_COLS * CELL_SIZE) && (y < N_ROWS * CELL_SIZE)) { // If click is on the grid

                if (e.getButton() == MouseEvent.BUTTON3) {              // On a right-button click
                    if (field[(cRow * N_COLS) + cCol] > MINE_CELL) {    //    If the cell is still covered (unknown value)
                        doRepaint = true;

                        if (field[(cRow * N_COLS) + cCol] <= COVERED_MINE_CELL) { // If cell isn't flagged
                            if (minesLeft > 0) {                       // If there are flags left
                                field[(cRow * N_COLS) + cCol] += MARK_FOR_CELL;   // Flag this cell
                                minesLeft--;                             // Decrement the available flags
                                String msg = Integer.toString(minesLeft); // Update the status to show the number of flags left
                                statusbar.setText(msg);
                            } else {                                   // If there aren't any flags left
                                statusbar.setText("No marks left");    // Update status to say "No marks left"
                            }
                        } else {                                       // If the cell is flagged
                            field[(cRow * N_COLS) + cCol] -= MARK_FOR_CELL;       // Remove the flag
                            minesLeft++;                               // Increment the number of available flags
                            String msg = Integer.toString(minesLeft);  // Update the status to show the number of flags left
                            statusbar.setText(msg);
                        }
                    }
                } else {                                              // If it's not a right click
                    if (field[(cRow * N_COLS) + cCol] > COVERED_MINE_CELL) {     // If it's flagged, ignore the click
                        return;
                    }

                    if ((field[(cRow * N_COLS) + cCol] > MINE_CELL)               // If the cell is unopened and it isn't marked
                            && (field[(cRow * N_COLS) + cCol] < MARKED_MINE_CELL)) {
                        field[(cRow * N_COLS) + cCol] -= COVER_FOR_CELL;          // Uncover (open) it and let the board know it's dirty (repaint)
                        doRepaint = true;

                        if (field[(cRow * N_COLS) + cCol] == MINE_CELL) {         // If the call contains a mine, mark the game as ended
                            inGame = false;
                        }

                        if (field[(cRow * N_COLS) + cCol] == EMPTY_CELL) {        // If the cell has no mines or neighbors with mines, open its neighbors. (Recursive call)
                            find_empty_cells((cRow * N_COLS) + cCol);
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
