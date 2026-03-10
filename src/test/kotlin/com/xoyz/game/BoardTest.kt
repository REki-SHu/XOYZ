package com.xoyz.game.core

import org.junit.Assert.*
import org.junit.Test

class BoardTest {

    // ─── Factory ──────────────────────────────────────────────────────────────

    @Test fun `empty board has 27 empty cells`() {
        val board = Board.empty()
        for (i in 0..26) {
            assertEquals(CellState.EMPTY, board.getCell(i))
        }
    }

    @Test fun `fromArray round-trips correctly`() {
        val cells = Array(27) { if (it % 3 == 0) CellState.X else CellState.EMPTY }
        val board  = Board.fromArray(cells)
        for (i in 0..26) assertEquals(cells[i], board.getCell(i))
    }

    // ─── withMove ─────────────────────────────────────────────────────────────

    @Test fun `withMove returns new board with symbol placed`() {
        val pos    = BoardPosition(0, 0, 0)
        val board  = Board.empty().withMove(pos, CellState.X)
        assertEquals(CellState.X, board.getCell(pos))
    }

    @Test fun `withMove does not mutate original board`() {
        val original = Board.empty()
        val pos      = BoardPosition(1, 1, 1)
        original.withMove(pos, CellState.O)
        assertEquals(CellState.EMPTY, original.getCell(pos))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `withMove on occupied cell throws`() {
        val pos   = BoardPosition(0, 0, 0)
        val board = Board.empty().withMove(pos, CellState.X)
        board.withMove(pos, CellState.O)  // should throw
    }

    // ─── Accessors ────────────────────────────────────────────────────────────

    @Test fun `isEmpty returns true for empty cell`() {
        val board = Board.empty()
        assertTrue(board.isEmpty(BoardPosition(2, 2, 2)))
    }

    @Test fun `isEmpty returns false for occupied cell`() {
        val pos   = BoardPosition(0, 0, 1)
        val board = Board.empty().withMove(pos, CellState.Y)
        assertFalse(board.isEmpty(pos))
    }

    @Test fun `isFull returns false for empty board`() {
        assertFalse(Board.empty().isFull)
    }

    @Test fun `isFull returns true when all cells occupied`() {
        var board = Board.empty()
        val symbols = listOf(CellState.X, CellState.O, CellState.Y, CellState.Z)
        for (i in 0..26) {
            val pos    = BoardPosition.fromFlatIndex(i)
            val symbol = symbols[i % symbols.size]
            board = board.withMove(pos, symbol)
        }
        assertTrue(board.isFull)
    }

    @Test fun `emptyCells returns all 27 on empty board`() {
        assertEquals(27, Board.empty().emptyCells().size)
    }

    @Test fun `emptyCells decreases by 1 after each move`() {
        val board = Board.empty().withMove(BoardPosition(0, 0, 0), CellState.X)
        assertEquals(26, board.emptyCells().size)
    }

    // ─── Bitmask ──────────────────────────────────────────────────────────────

    @Test fun `buildMask sets correct bits for X cells`() {
        val pos   = BoardPosition(0, 0, 0)   // flat index 0
        val board = Board.empty().withMove(pos, CellState.X)
        val mask  = board.buildMask { it == CellState.X }
        assertEquals(1, mask)   // bit 0 set
    }

    // ─── Equality ─────────────────────────────────────────────────────────────

    @Test fun `two empty boards are equal`() {
        assertEquals(Board.empty(), Board.empty())
    }

    @Test fun `boards with same moves are equal`() {
        val pos = BoardPosition(1, 2, 0)
        val b1  = Board.empty().withMove(pos, CellState.Z)
        val b2  = Board.empty().withMove(pos, CellState.Z)
        assertEquals(b1, b2)
    }
}