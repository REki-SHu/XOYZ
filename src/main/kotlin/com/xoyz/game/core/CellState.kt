package com.xoyz.game.core

enum class CellState(val symbol: String) {
    EMPTY(""),
    X("X"),
    O("O"),
    Y("Y"),
    Z("Z");

    val isPlayer1 get() = this == X || this == Y
    val isPlayer2 get() = this == O || this == Z
}