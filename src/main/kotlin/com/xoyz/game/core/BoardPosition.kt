package com.xoyz.game.core

import com.xoyz.game.utils.BoardConstants

/**
 * Represents a position on the 3×3×3 XOYZ board.
 *
 * Coordinates:
 *  - [layer] : 0 = top, 1 = middle, 2 = bottom
 *  - [row]   : 0 = front, 1 = center, 2 = back
 *  - [col]   : 0 = left,  1 = center, 2 = right
 *
 * The flattened 1-D index is computed as: layer*9 + row*3 + col
 */
data class BoardPosition(val layer: Int, val row: Int, val col: Int) {

    init {
        require(layer in 0 until BoardConstants.LAYERS)   { "layer must be 0..2, was $layer" }
        require(row   in 0 until BoardConstants.ROWS)     { "row must be 0..2, was $row"     }
        require(col   in 0 until BoardConstants.COLS)     { "col must be 0..2, was $col"     }
    }

    /** Flat index in the 27-element 1-D board array. */
    val flatIndex: Int
        get() = layer * BoardConstants.ROWS * BoardConstants.COLS +
                row   * BoardConstants.COLS +
                col

    override fun toString(): String = "BoardPosition(layer=$layer, row=$row, col=$col, flat=$flatIndex)"

    companion object {
        /**
         * Creates a [BoardPosition] from a flat index (0..26).
         * @throws IllegalArgumentException if the index is out of bounds.
         */
        fun fromFlatIndex(index: Int): BoardPosition {
            require(index in 0 until BoardConstants.TOTAL_CELLS) {
                "Flat index must be 0..26, was $index"
            }
            val layer = index / (BoardConstants.ROWS * BoardConstants.COLS)
            val rem   = index % (BoardConstants.ROWS * BoardConstants.COLS)
            val row   = rem / BoardConstants.COLS
            val col   = rem % BoardConstants.COLS
            return BoardPosition(layer, row, col)
        }
    }
}