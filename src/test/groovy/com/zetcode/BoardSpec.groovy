package com.zetcode

import spock.lang.Specification
import spock.lang.Unroll

import javax.swing.JLabel

class BoardSpec extends Specification {
    def board

    def setup() {
        board = new Board( new JLabel("placeholder") )
    }


    def "initBoard should set the board's preferred size, load the tile images, connect the mouse listener and start a new game"() {
        given: "a JLabel"
        def board = Spy(board )

        when: "an instance of Board is created using the label"
        board.initBoard()

        then: "setPreferredSize should have been called"
        1 * board.setPreferredSize(_)

        and: "addMouseListener should have been called"
        1 * board.addMouseListener(_)

        and: "the img array should be populated with " + board.NUM_IMAGES + " items"
        for(int i = 0; i < board.images.length; i++) {
            board.images[i] != null;
        }

        and: "newGame should have been called"
        1 * board.newGame()
    }

//    def "loadImages should load all the images needed by the game"() {
//        given: "an instance of Board"
//        def board = new Board( new JLabel("unused") )
//
//        when: "the loadImages method is called"
//        def images = board.loadImages()
//
//        then: "it should return an array of images needed by the game"
//        images.length ==  board.NUM_IMAGES
//    }

    // The field layout for a 4x5 field is as follows:
    //    0   1   2   3
    //    4   5   6   7
    //    8   9  10  11
    //   12  13  14  15
    //   16  17  18  19

    @Unroll
    def "when the field is 4x5 and getNeighbors() is called with cell #cell, it should return #expectedNeighbors"() {
        given: "an instance of the Board with four columns and five rows"
        board.initBoard(4, 5, 1)

        when: "calling getNeighbors(" + cell + "}"
        def neighbors = board.getNeighbors(cell)

        then: "it should return cells " + expectedNeighbors + " as its neighbors"
        neighbors.size() == expectedNeighbors.size()
        for(int index = 0; index++; index < expectedNeighbors.size()) {
            neighbors[index] == expectedNeighbors[index]
        }

        where:
        cell || expectedNeighbors
          0  || [1, 4, 5]
          2  || [1, 2, 5, 6, 7]
          3  || [2, 6, 7]
          8  || [4, 5, 9, 12, 13]
          9  || [4, 5, 6, 8, 10, 12, 13, 14]
         11  || [6, 7, 10, 14, 15]
         16  || [12, 13, 17]
         17  || [12, 13, 14, 16, 18]
         19  || [14, 15, 18]
    }


    def "getImageIndexWhenGameIsOver should return DRAW_MINE when the cell is mined and not flagged"() {
        given: "a mined cell that is covered and not flagged"
        board.initBoard(4, 5, 1)
        Cell cell = new Cell()
        cell.plantMine()

        when: "getImageIndexWhenGameIsOver is called with the cell and DRAW_WRONG_MARK as parameters"
        def imageIndex = board.getImageIndexWhenGameIsOver(cell, board.DRAW_WRONG_MARK)

        then: "imageIndex should be DRAW_MINE"
        imageIndex == board.DRAW_MINE
    }


    def "getImageIndexWhenGameIsOver should return DRAW_MARK when the cell is mined and flagged"() {
        given: "a mined cell that is covered and flagged"
        board.initBoard(4, 5, 1)
        Cell cell = new Cell()
        cell.plantMine()
        cell.toggleFlagged()

        when: "getImageIndexWhenGameIsOver is called with the cell and DRAW_WRONG_MARK as parameters"
        def imageIndex = board.getImageIndexWhenGameIsOver(cell, board.DRAW_WRONG_MARK)

        then: "imageIndex should be DRAW_MARK"
        imageIndex == board.DRAW_MARK
    }


    def "getImageIndexWhenGameIsOver should return DRAW_WRONG_MARK when the cell is not mined and flagged"() {
        given: "a mined cell that is covered and flagged"
        board.initBoard(4, 5, 1)
        Cell cell = new Cell()
        cell.toggleFlagged()

        when: "getImageIndexWhenGameIsOver is called with the cell and DRAW_MARK as parameters"
        def imageIndex = board.getImageIndexWhenGameIsOver(cell, board.DRAW_MARK)

        then: "imageIndex should be DRAW_WRONG_MARK"
        imageIndex == board.DRAW_WRONG_MARK
    }


    def "getImageIndexWhenGameIsOver should return DRAW_COVER when the cell is not mined"() {
        given: "a mined cell that is covered and not mined"
        board.initBoard(4, 5, 1)
        Cell cell = new Cell()

        when: "getImageIndexWhenGameIsOver is called with the cell and DRAW_WRONG_MARK as parameters"
        def imageIndex = board.getImageIndexWhenGameIsOver(cell, board.DRAW_WRONG_MARK)

        then: "imageIndex should be DRAW_COVER"
        imageIndex == board.DRAW_COVER
    }


    // The field layout for a 4x5 field with two adjoining empty (no mine or neighbors with mines) cells is as follows:
    //    0   1   2   3    .   .   .   X
    //    4   5   6   7    .   1   .   X
    //    8   9  10  11    .   .   2   .
    //   12  13  14  15    X   .   .   .
    //   16  17  18  19    X   .   .   .
    def "when opening an empty cell (no mine) if a neighboring cell is also empty. then open it"() {
        given: "a board with two adjoining empty cells"
        Board board = populateBoardWithMinesAt([3, 7, 12, 16])

        when: "first empty cell is opened"
        board.cells[5].uncover()
        board.find_empty_cells(5)

        then: "second empty cell should be opened"
        board.cells[10].isCovered() == false
    }


    def "when opening an empty cell (no mine) if a neighboring cell is also empty but flagged, then don't open it"() {
        given: "a board with two adjoining empty cells"
        Board board = populateBoardWithMinesAt([3, 7, 12, 16])

        and: "the second empty cell is flagged"
        board.cells[10].toggleFlagged()


        when: "first empty cell is opened"
        board.cells[5].uncover()
        board.find_empty_cells(5)

        then: "second empty cell should not be opened"
        board.cells[10].isUncovered() == false
    }


    Board populateBoardWithMinesAt(ArrayList<Integer> minedCells) {
        Board board = new Board(new JLabel(""))
        board.initBoard(5, 4, 0)

        for(int position = 0; position < board.cells.length; position++) {
            board.cells[position] = new Cell()
        }

        for(int position : minedCells) {
            Cell cell = board.cells[position]
            cell.plantMine()
        }

        return board
    }
}
