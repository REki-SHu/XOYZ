package com.xoyz.game.utils

import com.xoyz.game.core.CellState
import com.xoyz.game.core.Player

// ─── CellState extensions ─────────────────────────────────────────────────────

/**
 * Returns the [Player] from [player1] / [player2] who owns this [CellState],
 * or `null` if this state is [CellState.EMPTY].
 */
fun CellState.ownerIn(player1: Player, player2: Player): Player? = when {
    player1.owns(this) -> player1
    player2.owns(this) -> player2
    else               -> null
}

// ─── Int extensions ───────────────────────────────────────────────────────────

/**
 * Returns true if this Int bitmask has the bit at position [index] set.
 * Useful for quick cell-occupancy checks in the bitboard engine.
 */
fun Int.hasBit(index: Int): Boolean = (this and (1 shl index)) != 0