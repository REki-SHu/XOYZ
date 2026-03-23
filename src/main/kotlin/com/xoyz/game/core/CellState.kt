package com.xoyz.game.core

enum class CellState(val symbol: String) {
    EMPTY(""),
    X("X"),
    O("O"),
    Y("Y"),
    Z("Z");

    /** True if this is an actual placed symbol (not empty). */
    val isPlaced: Boolean get() = this != EMPTY
}