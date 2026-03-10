package com.xoyz.game.core

import com.xoyz.game.utils.WinningLinesTable
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests for [BitboardWinChecker].
 *
 * Strategy:
 *  1. For every one of the 49 lines, programmatically place a player's symbol
 *     on exactly those 3 cells and assert a win is detected.
 *  2. Verify that occupying only 2 of 3 cells does NOT trigger a win.
 *  3. Verify opponent placement on the third cell blocks the win.
 *  4. Smoke-test specific named lines (row, column, diagonal, space-diagonal).
 */
class WinCheckerTest {

    private lateinit var checker: BitboardWinChecker
    private lateinit var player1: Player
    private lateinit var player2: Player

    @Before fun setUp() {
        checker = BitboardWinChecker()
        player1 = Player.player1("Alice")
        player2 = Player.player2("Bob")
    }

    // ─── Core: all 49 lines ───────────────────────────────────────────────────

    @Test fun `player1 wins on every possible line using symbol X`() {
        WinningLinesTable.ALL_LINES.forEachIndexed { lineIdx, line ->
            val board = boardWithCells(line, CellState.X)
            val result = checker.findWinningLine(board, player1)
            assertNotNull("P1 should win on line $lineIdx ${line.toList()}", result)
        }
    }

    @Test fun `player1 wins on every possible line using symbol Y`() {
        WinningLinesTable.ALL_LINES.forEachIndexed { lineIdx, line ->
            val board = boardWithCells(line, CellState.Y)
            val result = checker.findWinningLine(board, player1)
            assertNotNull("P1 (Y) should win on line $lineIdx ${line.toList()}", result)
        }
    }

    @Test fun `player2 wins on every possible line using symbol O`() {
        WinningLinesTable.ALL_LINES.forEachIndexed { lineIdx, line ->
            val board = boardWithCells(line, CellState.O)
            val result = checker.findWinningLine(board, player2)
            assertNotNull("P2 should win on line $lineIdx ${line.toList()}", result)
        }
    }

    @Test fun `player2 wins on every possible line using symbol Z`() {
        WinningLinesTable.ALL_LINES.forEachIndexed { lineIdx, line ->
            val board = boardWithCells(line, CellState.Z)
            val result = checker.findWinningLine(board, player2)
            assertNotNull("P2 (Z) should win on line $lineIdx ${line.toList()}", result)
        }
    }

    // ─── Mixed symbols still win (same player owns both) ──────────────────────

    @Test fun `player1 wins with mixed X and Y on a line`() {
        // Place X on first two cells and Y on the third cell of line 0
        val line   = WinningLinesTable.ALL_LINES[0]
        val board  = boardWithMixedCells(
            line[0] to CellState.X,
            line[1] to CellState.Y,
            line[2] to CellState.X
        )
        val result = checker.findWinningLine(board, player1)
        assertNotNull("P1 should win with mixed X/Y", result)
    }

    @Test fun `player2 wins with mixed O and Z on a line`() {
        val line   = WinningLinesTable.ALL_LINES[0]
        val board  = boardWithMixedCells(
            line[0] to CellState.O,
            line[1] to CellState.Z,
            line[2] to CellState.O
        )
        val result = checker.findWinningLine(board, player2)
        assertNotNull("P2 should win with mixed O/Z", result)
    }

    // ─── No false positives ───────────────────────────────────────────────────

    @Test fun `no win on empty board`() {
        val board = Board.empty()
        assertNull(checker.findWinningLine(board, player1))
        assertNull(checker.findWinningLine(board, player2))
    }

    @Test fun `2 cells of a line is not a win`() {
        val line  = WinningLinesTable.ALL_LINES[0]
        val board = boardWithCells(intArrayOf(line[0], line[1]), CellState.X)
        assertNull("2 cells should not be a win", checker.findWinningLine(board, player1))
    }

    @Test fun `opponent blocking third cell prevents win`() {
        val line  = WinningLinesTable.ALL_LINES[0]
        val board = boardWithMixedCells(
            line[0] to CellState.X,
            line[1] to CellState.X,
            line[2] to CellState.O   // blocked by P2
        )
        assertNull("Blocked line should not be a win for P1",
            checker.findWinningLine(board, player1))
    }

    // ─── Winning line returned is correct ─────────────────────────────────────

    @Test fun `returned winning line matches placed line`() {
        val line   = WinningLinesTable.ALL_LINES[5]  // a layer diagonal
        val board  = boardWithCells(line, CellState.X)
        val result = checker.findWinningLine(board, player1)!!
        assertArrayEquals(line, result)
    }

    // ─── Named line smoke-tests ───────────────────────────────────────────────

    @Test fun `top layer first row win`() {
        // cells 0, 1, 2
        val board = boardWithCells(intArrayOf(0, 1, 2), CellState.X)
        assertNotNull(checker.findWinningLine(board, player1))
    }

    @Test fun `vertical column win layer 0 to 2 at position row0 col0`() {
        // cells 0, 9, 18
        val board = boardWithCells(intArrayOf(0, 9, 18), CellState.O)
        assertNotNull(checker.findWinningLine(board, player2))
    }

    @Test fun `space diagonal win`() {
        // cell (0,0,0)=0, (1,1,1)=13, (2,2,2)=26
        val board = boardWithCells(intArrayOf(0, 13, 26), CellState.Y)
        assertNotNull(checker.findWinningLine(board, player1))
    }

    @Test fun `space diagonal other direction`() {
        // cell (0,0,2)=2, (1,1,1)=13, (2,2,0)=24
        val board = boardWithCells(intArrayOf(2, 13, 24), CellState.Z)
        assertNotNull(checker.findWinningLine(board, player2))
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /** Builds a board with [symbol] placed at every index in [indices]. */
    private fun boardWithCells(indices: IntArray, symbol: CellState): Board {
        var board = Board.empty()
        indices.forEach { idx ->
            board = board.withMove(BoardPosition.fromFlatIndex(idx), symbol)
        }
        return board
    }

    /** Builds a board from a vararg list of (flatIndex to CellState) pairs. */
    private fun boardWithMixedCells(vararg placements: Pair<Int, CellState>): Board {
        var board = Board.empty()
        placements.forEach { (idx, symbol) ->
            board = board.withMove(BoardPosition.fromFlatIndex(idx), symbol)
        }
        return board
    }
}