package com.xoyz.game.core

/**
 * Contract for the complete rules engine.
 *
 * The rules engine evaluates a [Board] after each [Move] to determine the
 * new [GameState]. It delegates to [WinChecker] for win detection and
 * [MoveValidator] for legality checks — adhering to Single Responsibility.
 */
interface RulesEngine {
    /**
     * Evaluates [board] for the player who just placed [lastMove].
     *
     * Called *after* the move has been applied to the board.
     *
     * @return [GameState.Won] if [lastMove]'s player has a winning line,
     *         [GameState.Draw] if the board is full with no winner,
     *         [GameState.InProgress] otherwise.
     */
    fun evaluate(board: Board, lastMove: Move, players: List<Player>): GameState
}

/**
 * Standard [RulesEngine] implementation.
 *
 * Win evaluation order:
 *  1. Check whether the player who just moved has a winning line.
 *  2. If not, check whether the board is full (draw).
 *  3. Otherwise the game continues.
 */
class StandardRulesEngine(
    private val winChecker: WinChecker = BitboardWinChecker()
) : RulesEngine {

    override fun evaluate(board: Board, lastMove: Move, players: List<Player>): GameState {
        // Step 1 — Check win for the player who just moved
        val winningLine = winChecker.findWinningLine(board, lastMove.player)
        if (winningLine != null) {
            return GameState.Won(winner = lastMove.player, winningLine = winningLine)
        }

        // Step 2 — Check draw
        if (board.isFull) {
            return GameState.Draw
        }

        // Step 3 — Game continues
        return GameState.InProgress
    }
}