package com.xoyz.game.core

/**
 * Represents one of the 4 independent symbol-players in XOYZ.
 *
 * Each player owns exactly ONE symbol (X, O, Y, or Z).
 * A line of 3 identical symbols wins for that player.
 *
 * @property id     1..4
 * @property name   Display name (defaults to the symbol name)
 * @property symbol The single CellState this player places
 */
data class Player(
    val id: Int,
    val name: String,
    val symbol: CellState
) {
    init {
        require(id in 1..4)            { "Player id must be 1..4, was $id" }
        require(symbol != CellState.EMPTY) { "EMPTY is not a valid player symbol" }
    }

    /** Returns true if this player owns [state]. */
    fun owns(state: CellState): Boolean = state == symbol

    companion object {
        fun x(name: String = "X") = Player(1, name, CellState.X)
        fun o(name: String = "O") = Player(2, name, CellState.O)
        fun y(name: String = "Y") = Player(3, name, CellState.Y)
        fun z(name: String = "Z") = Player(4, name, CellState.Z)
    }
}