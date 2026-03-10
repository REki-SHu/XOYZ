package com.xoyz.game.core

/**
 * Represents the state of a single cell on the XOYZ board.
 *
 * Each cell can be empty or occupied by one of the four symbols.
 * Player 1 owns X and Y; Player 2 owns O and Z.
 */
enum class CellState {
    EMPTY,
    X,  // Player 1
    O,  // Player 2
    Y,  // Player 1
    Z;  // Player 2

    /** Returns true if this cell is occupied (not empty). */
    val isOccupied: Boolean get() = this != EMPTY

    /** Returns the display character for this cell state. */
    val displayChar: Char
        get() = when (this) {
            EMPTY -> '.'
            X     -> 'X'
            O     -> 'O'
            Y     -> 'Y'
            Z     -> 'Z'
        }
}