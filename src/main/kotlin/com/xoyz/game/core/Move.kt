package com.xoyz.game.core

/**
 * Represents a single player move in the XOYZ game.
 *
 * @property player   The player who made this move.
 * @property symbol   The symbol placed by the player (must be one they own).
 * @property position The board position where the symbol was placed.
 * @property timestamp Epoch millis when the move was recorded (defaults to now).
 */
data class Move(
    val player: Player,
    val symbol: CellState,
    val position: BoardPosition,
    val timestamp: Long = System.currentTimeMillis()
) {
    init {
        require(player.owns(symbol)) {
            "Player ${player.name} does not own symbol $symbol"
        }
        require(symbol != CellState.EMPTY) {
            "Cannot create a move with symbol EMPTY"
        }
    }

    override fun toString(): String =
        "${player.name} placed $symbol at $position"
}