package com.xoyz.game.utils

/**
 * Central repository for all board dimension constants.
 *
 * Having these in one place means every class — core logic, renderer,
 * tests — reads from the same source of truth.
 */
object BoardConstants {
    /** Number of layers (depth axis). */
    const val LAYERS: Int = 3

    /** Number of rows per layer. */
    const val ROWS: Int = 3

    /** Number of columns per layer. */
    const val COLS: Int = 3

    /** Total number of cells on the board. */
    const val TOTAL_CELLS: Int = LAYERS * ROWS * COLS  // 27

    /** Number of possible winning lines on a 3×3×3 cube. */
    const val TOTAL_WIN_LINES: Int = 49
}