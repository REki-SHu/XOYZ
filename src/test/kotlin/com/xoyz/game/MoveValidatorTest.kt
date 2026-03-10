package com.xoyz.game.core

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MoveValidatorTest {

    private lateinit var validator: DefaultMoveValidator
    private lateinit var p1: Player
    private lateinit var p2: Player
    private lateinit var emptyBoard: Board

    @Before fun setUp() {
        validator  = DefaultMoveValidator()
        p1         = Player.player1("Alice")
        p2         = Player.player2("Bob")
        emptyBoard = Board.empty()
    }

    @Test fun `valid move on empty cell returns Valid`() {
        val move   = Move(p1, CellState.X, BoardPosition(0, 0, 0))
        val result = validator.validate(emptyBoard, move, p1)
        assertTrue(result.isValid)
    }

    @Test fun `wrong player's turn returns Invalid`() {
        val move   = Move(p2, CellState.O, BoardPosition(0, 0, 0))
        val result = validator.validate(emptyBoard, move, p1)  // p1's turn
        assertFalse(result.isValid)
    }

    @Test fun `occupied cell returns Invalid`() {
        val pos    = BoardPosition(0, 0, 0)
        val board  = emptyBoard.withMove(pos, CellState.X)
        val move   = Move(p2, CellState.O, pos)
        val result = validator.validate(board, move, p2)
        assertFalse(result.isValid)
    }

    @Test fun `player using opponent symbol returns Invalid`() {
        val move   = Move(p1, CellState.O, BoardPosition(0, 0, 0))  // O belongs to p2
        val result = validator.validate(emptyBoard, move, p1)
        assertFalse(result.isValid)
    }

    @Test fun `player 1 can place Y`() {
        val move   = Move(p1, CellState.Y, BoardPosition(2, 2, 2))
        val result = validator.validate(emptyBoard, move, p1)
        assertTrue(result.isValid)
    }

    @Test fun `Invalid result contains human-readable reason`() {
        val move   = Move(p2, CellState.O, BoardPosition(0, 0, 0))
        val result = validator.validate(emptyBoard, move, p1) as ValidationResult.Invalid
        assertTrue(result.reason.isNotBlank())
    }
}