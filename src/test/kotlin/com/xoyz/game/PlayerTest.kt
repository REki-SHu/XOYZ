package com.xoyz.game.core

import org.junit.Assert.*
import org.junit.Test

class PlayerTest {

    @Test fun `player1 owns X and Y`() {
        val p = Player.player1()
        assertTrue(p.owns(CellState.X))
        assertTrue(p.owns(CellState.Y))
        assertFalse(p.owns(CellState.O))
        assertFalse(p.owns(CellState.Z))
    }

    @Test fun `player2 owns O and Z`() {
        val p = Player.player2()
        assertTrue(p.owns(CellState.O))
        assertTrue(p.owns(CellState.Z))
        assertFalse(p.owns(CellState.X))
        assertFalse(p.owns(CellState.Y))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `player with 3 symbols throws`() {
        Player(1, "Bad", setOf(CellState.X, CellState.Y, CellState.O))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `player id 0 throws`() {
        Player(0, "Bad", setOf(CellState.X, CellState.Y))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `player id 3 throws`() {
        Player(3, "Bad", setOf(CellState.X, CellState.Y))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `EMPTY as symbol throws`() {
        Player(1, "Bad", setOf(CellState.EMPTY, CellState.X))
    }
}