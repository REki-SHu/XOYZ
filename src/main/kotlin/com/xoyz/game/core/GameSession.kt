package com.xoyz.game.core

enum class GameStatus {
    IN_PROGRESS,
    PLAYER1_WINS,
    PLAYER2_WINS,
    DRAW
}

/**
 * Orchestrates a full game session.
 *
 * Turn order: Player 1 (X/Y) and Player 2 (O/Z) alternate.
 * Symbol selection rule (simple default): Player 1 alternates X→Y→X…,
 * Player 2 alternates O→Z→O…. This can be made player-choice later.
 */
class GameSession {
    val board = Board()
    var status: GameStatus = GameStatus.IN_PROGRESS
        private set

    var isPlayer1Turn: Boolean = true
        private set

    private var p1MoveCount = 0
    private var p2MoveCount = 0

    /** Returns true if the move was accepted, false if invalid. */
    fun makeMove(layer: Int, row: Int, col: Int): Boolean {
        if (status != GameStatus.IN_PROGRESS) return false
        if (!RulesEngine.isValidMove(board, layer, row, col)) return false

        val symbol = nextSymbol()
        board.setCell(layer, row, col, symbol)

        if (isPlayer1Turn) p1MoveCount++ else p2MoveCount++

        val flat = board.getFlat()
        status = when {
            RulesEngine.checkWin(flat) { it.isPlayer1 } -> GameStatus.PLAYER1_WINS
            RulesEngine.checkWin(flat) { it.isPlayer2 } -> GameStatus.PLAYER2_WINS
            board.isFull()                              -> GameStatus.DRAW
            else                                        -> GameStatus.IN_PROGRESS
        }

        if (status == GameStatus.IN_PROGRESS) isPlayer1Turn = !isPlayer1Turn
        return true
    }

    /** Simple alternating symbol selection: X Y X Y … / O Z O Z … */
    private fun nextSymbol(): CellState = if (isPlayer1Turn) {
        if (p1MoveCount % 2 == 0) CellState.X else CellState.Y
    } else {
        if (p2MoveCount % 2 == 0) CellState.O else CellState.Z
    }
}