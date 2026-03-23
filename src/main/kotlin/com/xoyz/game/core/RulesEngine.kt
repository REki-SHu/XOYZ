package com.xoyz.game.core

/**
 * Win detection using precomputed index arrays (49 lines on a 3×3×3 cube).
 * Each entry is a triple of flat indices (layer*9 + row*3 + col).
 *
 * A player wins if ANY line has all three cells belonging to that player
 * (either of their two symbols).
 */
object RulesEngine {

    // All 49 winning lines as flat index triples
    val winLines: List<IntArray> = buildWinLines()

    private fun buildWinLines(): List<IntArray> {
        val lines = mutableListOf<IntArray>()

        fun idx(l: Int, r: Int, c: Int) = l * 9 + r * 3 + c

        // --- Within each layer: rows, columns, 2 diagonals (3 layers × 8 = 24) ---
        for (l in 0..2) {
            // Rows
            for (r in 0..2) lines.add(intArrayOf(idx(l,r,0), idx(l,r,1), idx(l,r,2)))
            // Columns
            for (c in 0..2) lines.add(intArrayOf(idx(l,0,c), idx(l,1,c), idx(l,2,c)))
            // Diagonals
            lines.add(intArrayOf(idx(l,0,0), idx(l,1,1), idx(l,2,2)))
            lines.add(intArrayOf(idx(l,0,2), idx(l,1,1), idx(l,2,0)))
        }

        // --- Vertical columns through layers (3×3 = 9) ---
        for (r in 0..2)
            for (c in 0..2)
                lines.add(intArrayOf(idx(0,r,c), idx(1,r,c), idx(2,r,c)))

        // --- Vertical plane diagonals (row-planes + col-planes = 12) ---
        for (r in 0..2) {
            lines.add(intArrayOf(idx(0,r,0), idx(1,r,1), idx(2,r,2)))
            lines.add(intArrayOf(idx(0,r,2), idx(1,r,1), idx(2,r,0)))
        }
        for (c in 0..2) {
            lines.add(intArrayOf(idx(0,0,c), idx(1,1,c), idx(2,2,c)))
            lines.add(intArrayOf(idx(0,2,c), idx(1,1,c), idx(2,0,c)))
        }

        // --- Space diagonals (4) ---
        lines.add(intArrayOf(idx(0,0,0), idx(1,1,1), idx(2,2,2)))
        lines.add(intArrayOf(idx(0,0,2), idx(1,1,1), idx(2,2,0)))
        lines.add(intArrayOf(idx(0,2,0), idx(1,1,1), idx(2,0,2)))
        lines.add(intArrayOf(idx(0,2,2), idx(1,1,1), idx(2,0,0)))

        return lines  // 24 + 9 + 12 + 4 = 49 ✓
    }

    /**
     * Returns true if [board] contains a winning line for a player
     * whose cells satisfy [isPlayerCell].
     */
    fun checkWin(board: Array<CellState>, isPlayerCell: (CellState) -> Boolean): Boolean {
        for (line in winLines) {
            if (line.all { isPlayerCell(board[it]) }) return true
        }
        return false
    }

    fun isValidMove(board: Board, layer: Int, row: Int, col: Int): Boolean =
        board.isCellEmpty(layer, row, col)
}