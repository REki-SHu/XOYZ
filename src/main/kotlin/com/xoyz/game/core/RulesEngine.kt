package com.xoyz.game.core

/**
 * Win detection for XOYZ: a line is won when all 3 cells contain
 * the EXACT SAME symbol (X-X-X, O-O-O, Y-Y-Y, or Z-Z-Z).
 *
 * X, O, Y, Z are 4 independent symbols — mixed lines do NOT win.
 */
object RulesEngine {

    val winLines: List<IntArray> = buildWinLines()

    private fun buildWinLines(): List<IntArray> {
        val lines = mutableListOf<IntArray>()

        fun idx(l: Int, r: Int, c: Int) = l * 9 + r * 3 + c

        // Rows within each layer (9)
        for (l in 0..2)
            for (r in 0..2)
                lines.add(intArrayOf(idx(l,r,0), idx(l,r,1), idx(l,r,2)))

        // Columns within each layer (9)
        for (l in 0..2)
            for (c in 0..2)
                lines.add(intArrayOf(idx(l,0,c), idx(l,1,c), idx(l,2,c)))

        // Diagonals within each layer (6)
        for (l in 0..2) {
            lines.add(intArrayOf(idx(l,0,0), idx(l,1,1), idx(l,2,2)))
            lines.add(intArrayOf(idx(l,0,2), idx(l,1,1), idx(l,2,0)))
        }

        // Vertical columns through layers (9)
        for (r in 0..2)
            for (c in 0..2)
                lines.add(intArrayOf(idx(0,r,c), idx(1,r,c), idx(2,r,c)))

        // Vertical plane row-diagonals (6)
        for (r in 0..2) {
            lines.add(intArrayOf(idx(0,r,0), idx(1,r,1), idx(2,r,2)))
            lines.add(intArrayOf(idx(0,r,2), idx(1,r,1), idx(2,r,0)))
        }

        // Vertical plane col-diagonals (6)
        for (c in 0..2) {
            lines.add(intArrayOf(idx(0,0,c), idx(1,1,c), idx(2,2,c)))
            lines.add(intArrayOf(idx(0,2,c), idx(1,1,c), idx(2,0,c)))
        }

        // Space diagonals (4)
        lines.add(intArrayOf(idx(0,0,0), idx(1,1,1), idx(2,2,2)))
        lines.add(intArrayOf(idx(0,0,2), idx(1,1,1), idx(2,2,0)))
        lines.add(intArrayOf(idx(0,2,0), idx(1,1,1), idx(2,0,2)))
        lines.add(intArrayOf(idx(0,2,2), idx(1,1,1), idx(2,0,0)))

        return lines // 9+9+6+9+6+6+4 = 49
    }

    /**
     * Checks if [symbol] has won — i.e. any line has all 3 cells == [symbol].
     * This is the core XOYZ rule: same symbol must fill the entire line.
     */
    fun checkWin(board: Array<CellState>, symbol: CellState): Boolean {
        if (symbol == CellState.EMPTY) return false
        for (line in winLines) {
            if (line.all { board[it] == symbol }) return true
        }
        return false
    }

    fun isValidMove(board: Board, layer: Int, row: Int, col: Int): Boolean =
        board.isCellEmpty(layer, row, col)
}