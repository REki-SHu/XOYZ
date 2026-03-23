package com.xoyz.game.core

import com.xoyz.game.utils.WinningLinesTable

/**
 * Contract for determining whether a player has won after a move.
 */
interface WinChecker {
    /**
     * Checks whether [player] has achieved a winning line on [board].
     *
     * @return The matching [IntArray] of flat indices (the winning line),
     *         or `null` if the player has not yet won.
     */
    fun findWinningLine(board: Board, player: Player): IntArray?
}

/**
 * Bitboard-based [WinChecker].
 *
 * Algorithm:
 *  1. Build an Int bitmask where bit i is set for every cell owned by [player].
 *  2. Iterate over the 49 precomputed winning masks from [WinningLinesTable].
 *  3. If (playerMask AND winMask) == winMask → win detected.
 *
 * Complexity: O(27) to build mask + O(49) AND checks — effectively constant.
 */
class BitboardWinChecker : WinChecker {

    override fun findWinningLine(board: Board, player: Player): IntArray? {
        // Build the player's occupancy bitmask by iterating all 27 flat indices
        var playerMask = 0
        val flat = board.getFlat()
        flat.forEachIndexed { index, cellState ->
            if (player.owns(cellState)) {
                playerMask = playerMask or (1 shl index)
            }
        }

        // Check all 49 precomputed masks
        WinningLinesTable.ALL_MASKS.forEachIndexed { lineIndex, winMask ->
            if ((playerMask and winMask) == winMask) {
                return WinningLinesTable.ALL_LINES[lineIndex]
            }
        }
        return null
    }
}