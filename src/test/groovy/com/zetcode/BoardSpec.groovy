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

    // The field layout for a 4x5 field is as follows"
    //    0   1   2   3
    //    4   5   6   7
    //    8   9  10  11
    //   12  13  14  15
    //   16  17  18  19


    def "when the field is 4x5 and getNeighbors() is called with cell 0, it should return cells 1, 4, and 5"() {
        given: "an instance of the Board with four columns and five rows"
        board.initBoard(4, 5, 1)

        when: "calling getNeighbors() with 0 as the parameter"
        def neighbors = board.getNeighbors(0)

        then: "it should return cells 1, 4, and 5 as its neighbors"
        neighbors.size() == 3
        neighbors[0] == 1
        neighbors[1] == 4
        neighbors[2] == 5
    }

    def "when the field is 4x5 and getNeighbors() is called with cell 3, it should return cells 2, 6, and 7"() {
        given: "an instance of the Board with four columns and five rows"
        board.initBoard(4, 5, 1)

        when: "calling getNeighbors() with 3 as the parameter"
        def neighbors = board.getNeighbors(3)

        then: "it should return cells 2, 6, and 7 as its neighbors"
        neighbors.size() == 3
        neighbors[0] == 2
        neighbors[1] == 6
        neighbors[2] == 7
    }

    def "when the field is 4x5 and getNeighbors() is called with cell 16, it should return cells 12, 3, and 17"() {
        given: "an instance of the Board with four columns and five rows"
        board.initBoard(4, 5, 1)

        when: "calling getNeighbors() with 16 as the parameter"
        def neighbors = board.getNeighbors(16)

        then: "it should return cells 12, 13, and 17 as its neighbors"
        neighbors.size() == 3
        neighbors[0] == 12
        neighbors[1] == 13
        neighbors[2] == 17
    }


    def "when the field is 4x5 and getNeighbors() is called with cell 19, it should return cells 14, 15, and 18"() {
        given: "an instance of the Board with four columns and five rows"
        board.initBoard(4, 5, 1)

        when: "calling getNeighbors() with 19 as the parameter"
        def neighbors = board.getNeighbors(19)

        then: "it should return cells 14, 15, and 18 as its neighbors"
        neighbors.size() == 3
        neighbors[0] == 14
        neighbors[1] == 15
        neighbors[2] == 18
    }

    def "when the field is 4x5 and getNeighbors() is called with cell 2, it should return cells 1, 3, 5, 6, and 7"() {
        given: "an instance of the Board with four columns and five rows"
        board.initBoard(4, 5, 1)

        when: "calling getNeighbors() with 2 as the parameter"
        def neighbors = board.getNeighbors(2)

        then: "it should return cells 1, 3, 5, 6, and 7 as its neighbors"
        neighbors.size() == 5
        neighbors[0] == 1
        neighbors[1] == 3
        neighbors[2] == 5
        neighbors[3] == 6
        neighbors[4] == 7
    }

}
