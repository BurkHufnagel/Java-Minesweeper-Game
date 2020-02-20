package com.zetcode

import spock.lang.Specification

import javax.swing.JLabel

class BoardSpec extends Specification {

    def "initBoard should set the board's preferred size, load the tile images, connect the mouse listener and start a new game"() {
        given: "a JLabel"
        def JLabel jLabel = new JLabel("placeholder")
        def board = Spy(new Board(jLabel) )

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

    def "loadImages should load all the images needed by the game"() {
        given: "an instance of Board"
        def board = new Board( new JLabel("unused") )

        when: "the loadImages method is called"
        def images = board.loadImages()

        then: "it should return an array of images needed by the game"
        images.length ==  board.NUM_IMAGES
    }
}
