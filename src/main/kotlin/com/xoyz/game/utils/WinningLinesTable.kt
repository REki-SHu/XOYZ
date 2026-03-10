package com.xoyz.game.utils

/**
 * Precomputed lookup table for all 49 winning lines on a 3×3×3 XOYZ board.
 *
 * ─────────────────────────────────────────────────────────────────────────────
 * COORDINATE SYSTEM
 * ─────────────────────────────────────────────────────────────────────────────
 * The board is addressed as [layer][row][col] where each dimension is 0, 1, 2.
 * Flat index = layer*9 + row*3 + col  (0 … 26)
 *
 * ─────────────────────────────────────────────────────────────────────────────
 * LINE CATEGORIES  (49 total)
 * ─────────────────────────────────────────────────────────────────────────────
 *  A. Rows within a layer          (3 rows  × 3 layers)          =  9
 *  B. Columns within a layer       (3 cols  × 3 layers)          =  9
 *  C. Diagonals within a layer     (2 diags × 3 layers)          =  6
 *  D. Vertical columns             (3 rows  × 3 cols )           =  9
 *  E. Vertical-plane row-diagonals (2 diags × 3 col-planes)      =  6
 *  F. Vertical-plane col-diagonals (2 diags × 3 row-planes)      =  6
 *  G. Space diagonals              (4 main cube diagonals)        =  4
 *                                                           Total = 49
 */
object WinningLinesTable {

    /**
     * All 49 winning lines represented as arrays of three flat indices.
     *
     * A win is detected by checking that all three cells in any line are
     * occupied by the *same* player (either symbol of that player counts).
     */
    val ALL_LINES: Array<IntArray> = buildLines()

    /**
     * Bitmask representation of every winning line.
     * Each Int has exactly 3 bits set — the positions occupied by that line.
     * Useful for O(49) bitwise win-checking.
     */
    val ALL_MASKS: IntArray = ALL_LINES.map { line ->
        line.fold(0) { acc, idx -> acc or (1 shl idx) }
    }.toIntArray()

    // ─── Builders ────────────────────────────────────────────────────────────

    private fun buildLines(): Array<IntArray> {
        val lines = mutableListOf<IntArray>()

        lines += buildRowLines()
        lines += buildColumnLines()
        lines += buildLayerDiagonalLines()
        lines += buildVerticalColumnLines()
        lines += buildVerticalPlaneRowDiagonals()
        lines += buildVerticalPlaneColDiagonals()
        lines += buildSpaceDiagonals()

        require(lines.size == 49) {
            "Expected 49 winning lines, got ${lines.size}"
        }
        return lines.toTypedArray()
    }

    /** A. Rows within each layer — 3 rows × 3 layers = 9 lines */
    private fun buildRowLines(): List<IntArray> = buildList {
        for (layer in 0..2) {
            for (row in 0..2) {
                add(intArrayOf(idx(layer, row, 0), idx(layer, row, 1), idx(layer, row, 2)))
            }
        }
    }

    /** B. Columns within each layer — 3 cols × 3 layers = 9 lines */
    private fun buildColumnLines(): List<IntArray> = buildList {
        for (layer in 0..2) {
            for (col in 0..2) {
                add(intArrayOf(idx(layer, 0, col), idx(layer, 1, col), idx(layer, 2, col)))
            }
        }
    }

    /** C. Diagonals within each layer — 2 diags × 3 layers = 6 lines */
    private fun buildLayerDiagonalLines(): List<IntArray> = buildList {
        for (layer in 0..2) {
            // Top-left → bottom-right
            add(intArrayOf(idx(layer, 0, 0), idx(layer, 1, 1), idx(layer, 2, 2)))
            // Top-right → bottom-left
            add(intArrayOf(idx(layer, 0, 2), idx(layer, 1, 1), idx(layer, 2, 0)))
        }
    }

    /** D. Vertical columns (same row & col, all layers) — 3×3 = 9 lines */
    private fun buildVerticalColumnLines(): List<IntArray> = buildList {
        for (row in 0..2) {
            for (col in 0..2) {
                add(intArrayOf(idx(0, row, col), idx(1, row, col), idx(2, row, col)))
            }
        }
    }

    /**
     * E. Vertical-plane diagonals across layers, varying row — 2 × 3 = 6 lines.
     * For each fixed col, the plane contains layers 0-2 and rows 0-2.
     */
    private fun buildVerticalPlaneRowDiagonals(): List<IntArray> = buildList {
        for (col in 0..2) {
            // layer increases, row increases: (0,0,c)→(1,1,c)→(2,2,c)
            add(intArrayOf(idx(0, 0, col), idx(1, 1, col), idx(2, 2, col)))
            // layer increases, row decreases: (0,2,c)→(1,1,c)→(2,0,c)
            add(intArrayOf(idx(0, 2, col), idx(1, 1, col), idx(2, 0, col)))
        }
    }

    /**
     * F. Vertical-plane diagonals across layers, varying col — 2 × 3 = 6 lines.
     * For each fixed row, the plane contains layers 0-2 and cols 0-2.
     */
    private fun buildVerticalPlaneColDiagonals(): List<IntArray> = buildList {
        for (row in 0..2) {
            // layer increases, col increases: (0,r,0)→(1,r,1)→(2,r,2)
            add(intArrayOf(idx(0, row, 0), idx(1, row, 1), idx(2, row, 2)))
            // layer increases, col decreases: (0,r,2)→(1,r,1)→(2,r,0)
            add(intArrayOf(idx(0, row, 2), idx(1, row, 1), idx(2, row, 0)))
        }
    }

    /**
     * G. The four main space diagonals of the cube — 4 lines.
     *
     * Each diagonal connects one corner of the cube to the opposite corner,
     * passing through the center cell (1,1,1).
     */
    private fun buildSpaceDiagonals(): List<IntArray> = listOf(
        intArrayOf(idx(0, 0, 0), idx(1, 1, 1), idx(2, 2, 2)),  // front-top-left  → back-bottom-right
        intArrayOf(idx(0, 0, 2), idx(1, 1, 1), idx(2, 2, 0)),  // front-top-right → back-bottom-left
        intArrayOf(idx(0, 2, 0), idx(1, 1, 1), idx(2, 0, 2)),  // front-bot-left  → back-top-right
        intArrayOf(idx(0, 2, 2), idx(1, 1, 1), idx(2, 0, 0))   // front-bot-right → back-top-left
    )

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /** Converts 3-D coordinates to a flat index. */
    private fun idx(layer: Int, row: Int, col: Int): Int =
        layer * 9 + row * 3 + col
}