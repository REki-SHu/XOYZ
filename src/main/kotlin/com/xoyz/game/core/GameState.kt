package com.xoyz.game.core

/**
 * Represents the current state of the game lifecycle.
 */
sealed class GameState {

    /** The game is ongoing; moves can still be made. */
    object InProgress : GameState() {
        override fun toString() = "InProgress"
    }

    /**
     * A player has achieved a winning line.
     *
     * @property winner      The player who won.
     * @property winningLine The three flat-indices that form the winning line.
     */
    data class Won(val winner: Player, val winningLine: IntArray) : GameState() {
        override fun toString() = "Won(${winner.name})"
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Won) return false
            return winner == other.winner && winningLine.contentEquals(other.winningLine)
        }
        override fun hashCode(): Int = 31 * winner.hashCode() + winningLine.contentHashCode()
    }

    /** All 27 cells are filled and no player has a winning line — the game is a draw. */
    object Draw : GameState() {
        override fun toString() = "Draw"
    }
}