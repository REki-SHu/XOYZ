package com.xoyz.game.core

/**
 * Contract for determining whose turn it is and advancing the turn after a move.
 */
interface TurnManager {
    /** The player who should move next. */
    val currentPlayer: Player

    /**
     * Returns a new [TurnManager] that has advanced to the next player's turn.
     * The original instance is not mutated (immutable design mirrors [Board]).
     */
    fun advance(): TurnManager
}

/**
 * Simple alternating [TurnManager].
 *
 * Player 1 always goes first. After each move the active player
 * switches to the other.
 *
 * @param players     Ordered list of the two players; index 0 goes first.
 * @param activeIndex Index into [players] for the current turn.
 */
class AlternatingTurnManager(
    private val players: List<Player>,
    private val activeIndex: Int = 0
) : TurnManager {

    init {
        require(players.size == 2) { "AlternatingTurnManager requires exactly 2 players" }
        require(activeIndex in players.indices) { "activeIndex out of bounds" }
    }

    override val currentPlayer: Player
        get() = players[activeIndex]

    override fun advance(): TurnManager =
        AlternatingTurnManager(players, (activeIndex + 1) % players.size)

    /** Convenience: returns the player who is *not* currently active. */
    val waitingPlayer: Player
        get() = players[(activeIndex + 1) % players.size]
}