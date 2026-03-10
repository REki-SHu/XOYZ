package com.xoyz.game.core

/**
 * Represents a player in the XOYZ game.
 *
 * @property id      Unique player identifier (1 or 2).
 * @property name    Display name chosen by the player.
 * @property symbols The two symbols this player is allowed to place.
 */
data class Player(
    val id: Int,
    val name: String,
    val symbols: Set<CellState>
) {
    init {
        require(id in 1..2)            { "Player id must be 1 or 2, was $id" }
        require(symbols.size == 2)     { "Each player must have exactly 2 symbols" }
        require(CellState.EMPTY !in symbols) { "EMPTY is not a valid player symbol" }
    }

    /** Returns true if this player owns [symbol]. */
    fun owns(symbol: CellState): Boolean = symbol in symbols

    companion object {
        /** Default Player 1 with symbols X and Y. */
        fun player1(name: String = "Player 1"): Player =
            Player(id = 1, name = name, symbols = setOf(CellState.X, CellState.Y))

        /** Default Player 2 with symbols O and Z. */
        fun player2(name: String = "Player 2"): Player =
            Player(id = 2, name = name, symbols = setOf(CellState.O, CellState.Z))
    }
}