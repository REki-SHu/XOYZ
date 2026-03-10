package com.xoyz.game.core

/**
 * Contract for validating a proposed [Move] against the current [Board]
 * and the [Player] whose turn it is.
 *
 * Keeping validation behind an interface allows the rules to be swapped
 * or mocked in tests without touching the [GameSession].
 */
interface MoveValidator {
    /**
     * Returns a [ValidationResult] describing whether [move] is legal.
     *
     * @param board         The current board snapshot.
     * @param move          The proposed move.
     * @param currentPlayer The player whose turn it currently is.
     */
    fun validate(board: Board, move: Move, currentPlayer: Player): ValidationResult
}

/**
 * Outcome of a move validation.
 */
sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()

    val isValid: Boolean get() = this is Valid
}

/**
 * Default implementation of [MoveValidator].
 *
 * Checks:
 *  1. It is the correct player's turn.
 *  2. The player owns the symbol they want to place.
 *  3. The target cell is empty.
 *  4. The game is still in progress (enforced by [GameSession]).
 */
class DefaultMoveValidator : MoveValidator {

    override fun validate(
        board: Board,
        move: Move,
        currentPlayer: Player
    ): ValidationResult {

        if (move.player.id != currentPlayer.id) {
            return ValidationResult.Invalid(
                "It is ${currentPlayer.name}'s turn, not ${move.player.name}'s turn."
            )
        }

        if (!move.player.owns(move.symbol)) {
            return ValidationResult.Invalid(
                "${move.player.name} does not own symbol ${move.symbol}."
            )
        }

        if (!board.isEmpty(move.position)) {
            val existing = board.getCell(move.position)
            return ValidationResult.Invalid(
                "Cell ${move.position} is already occupied by $existing."
            )
        }

        return ValidationResult.Valid
    }
}