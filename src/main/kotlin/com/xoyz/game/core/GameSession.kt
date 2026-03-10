package com.xoyz.game.core

/**
 * Result of attempting to apply a [Move] to a [GameSession].
 */
sealed class MoveResult {
    /** Move was accepted; [newSession] is the updated session. */
    data class Accepted(val newSession: GameSession) : MoveResult()

    /** Move was rejected; [reason] explains why. */
    data class Rejected(val reason: String) : MoveResult()
}

/**
 * Immutable snapshot of a complete game session.
 *
 * [GameSession] orchestrates [Board], [TurnManager], [MoveValidator],
 * [RulesEngine], and [Move] history following the SOLID principles:
 *
 *  - **S** — Each concern lives in its own class.
 *  - **O** — Swap implementations via constructor injection.
 *  - **L** — All collaborators are interchangeable by their interfaces.
 *  - **I** — Narrow interfaces: [WinChecker], [MoveValidator], [TurnManager].
 *  - **D** — [GameSession] depends on abstractions, not concrete classes.
 *
 * Applying a move returns a *new* [GameSession]; the original is unchanged.
 * This makes the history list trivially safe to inspect or undo.
 *
 * @param player1       First player (always moves first).
 * @param player2       Second player.
 * @param board         Current board snapshot.
 * @param turnManager   Tracks whose turn it is.
 * @param moveValidator Validates move legality.
 * @param rulesEngine   Evaluates win/draw conditions.
 * @param history       Ordered list of moves applied so far.
 * @param state         Current [GameState].
 */
class GameSession private constructor(
    val player1: Player,
    val player2: Player,
    val board: Board,
    private val turnManager: TurnManager,
    private val moveValidator: MoveValidator,
    private val rulesEngine: RulesEngine,
    val history: List<Move>,
    val state: GameState
) {

    /** The player who should move next (only meaningful while [state] is [GameState.InProgress]). */
    val currentPlayer: Player get() = turnManager.currentPlayer

    /** Convenience: returns true if the game is still going. */
    val isInProgress: Boolean get() = state is GameState.InProgress

    // ─── Core operation ───────────────────────────────────────────────────────

    /**
     * Attempts to apply [move] to this session.
     *
     * @return [MoveResult.Accepted] with a fully updated [GameSession], or
     *         [MoveResult.Rejected] with a human-readable reason.
     */
    fun applyMove(move: Move): MoveResult {
        if (!isInProgress) {
            return MoveResult.Rejected("The game has already ended: $state")
        }

        val validation = moveValidator.validate(board, move, currentPlayer)
        if (!validation.isValid) {
            return MoveResult.Rejected((validation as ValidationResult.Invalid).reason)
        }

        val newBoard    = board.withMove(move.position, move.symbol)
        val newHistory  = history + move
        val newState    = rulesEngine.evaluate(newBoard, move, listOf(player1, player2))
        val newTurn     = turnManager.advance()

        val newSession = GameSession(
            player1        = player1,
            player2        = player2,
            board          = newBoard,
            turnManager    = newTurn,
            moveValidator  = moveValidator,
            rulesEngine    = rulesEngine,
            history        = newHistory,
            state          = newState
        )
        return MoveResult.Accepted(newSession)
    }

    // ─── Factory ──────────────────────────────────────────────────────────────

    companion object {

        /**
         * Creates a brand-new game session with the default collaborators.
         *
         * @param player1Name Display name for Player 1 (default "Player 1").
         * @param player2Name Display name for Player 2 (default "Player 2").
         */
        fun newGame(
            player1Name: String = "Player 1",
            player2Name: String = "Player 2"
        ): GameSession = create(
            player1 = Player.player1(player1Name),
            player2 = Player.player2(player2Name)
        )

        /**
         * Creates a game session with custom [Player] objects and optionally
         * custom collaborator implementations (useful for testing).
         */
        fun create(
            player1: Player,
            player2: Player,
            board: Board             = Board.empty(),
            moveValidator: MoveValidator = DefaultMoveValidator(),
            rulesEngine: RulesEngine     = StandardRulesEngine(),
            history: List<Move>          = emptyList(),
            state: GameState             = GameState.InProgress
        ): GameSession {
            val turnManager: TurnManager = AlternatingTurnManager(listOf(player1, player2))
            return GameSession(
                player1       = player1,
                player2       = player2,
                board         = board,
                turnManager   = turnManager,
                moveValidator = moveValidator,
                rulesEngine   = rulesEngine,
                history       = history,
                state         = state
            )
        }
    }
}