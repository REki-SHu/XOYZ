package com.xoyz.game.core

import com.xoyz.game.utils.WinningLinesTable

/**
 * Contract for win detection.
 */
interface WinChecker {
    /**
     * Returns the winning line (flat indices) if [symbol] has won on [board],
     * or null if not.
     */
    fun findWinningLine(board: Board, symbol: CellState): IntArray?
}

/**
 * Bitboard-based [WinChecker].
 *
 * Builds an occupancy bitmask for [symbol] specifically (not a player group),
 * then checks all 49 precomputed masks. A win requires all 3 cells in a line
 * to be the exact same [symbol].
 */
class BitboardWinChecker : WinChecker {

    override fun findWinningLine(board: Board, symbol: CellState): IntArray? {
        if (symbol == CellState.EMPTY) return null

        // Build bitmask: bit i set where board[i] == symbol exactly
        var mask = 0
        board.getFlat().forEachIndexed { index, cellState ->
            if (cellState == symbol) {
                mask = mask or (1 shl index)
            }
        }

        // Check all 49 precomputed win masks
        WinningLinesTable.ALL_MASKS.forEachIndexed { lineIndex, winMask ->
            if ((mask and winMask) == winMask) {
                return WinningLinesTable.ALL_LINES[lineIndex]
            }
        }
        return null
    }
}