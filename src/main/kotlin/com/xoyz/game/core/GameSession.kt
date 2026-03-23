package com.xoyz.game.core

/**
 * Orchestrates a full XOYZ game session.
 *
 * Turn order: X → O → Y → Z → X → …  (4 independent symbols)
 * Win condition: 3 of the SAME symbol in any of the 49 lines.
 */
class GameSession {

    val board = Board()

    var status: GameStatus = GameStatus.IN_PROGRESS
        private set

    // Turn index cycles 0→1→2→3→0…  maps to X, O, Y, Z
    private var turnIndex: Int = 0

    /** The symbol whose turn it currently is. */
    val currentSymbol: CellState
        get() = TURN_ORDER[turnIndex]

    /** Returns true if the move was accepted, false if invalid. */
    fun makeMove(layer: Int, row: Int, col: Int): Boolean {
        if (status != GameStatus.IN_PROGRESS) return false
        if (!RulesEngine.isValidMove(board, layer, row, col)) return false

        val symbol = currentSymbol
        board.setCell(layer, row, col, symbol)

        val flat = board.getFlat()
        status = when {
            RulesEngine.checkWin(flat, CellState.X) -> GameStatus.X_WINS
            RulesEngine.checkWin(flat, CellState.O) -> GameStatus.O_WINS
            RulesEngine.checkWin(flat, CellState.Y) -> GameStatus.Y_WINS
            RulesEngine.checkWin(flat, CellState.Z) -> GameStatus.Z_WINS
            board.isFull()                          -> GameStatus.DRAW
            else                                    -> GameStatus.IN_PROGRESS
        }

        if (status == GameStatus.IN_PROGRESS) turnIndex = (turnIndex + 1) % 4
        return true
    }

    companion object {
        /** Fixed turn order: X, O, Y, Z */
        val TURN_ORDER = arrayOf(
            CellState.X,
            CellState.O,
            CellState.Y,
            CellState.Z
        )
    }
}