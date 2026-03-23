package com.xoyz.game.core

/**
 * Contract for validating a proposed [Move] against the current [Board]
 * and the [Player] whose turn it is.
 */
interface MoveValidator {
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

        // Unpack BoardPosition into the 3 Int parameters Board.getCell() expects
        val pos = move.position
        val existing = board.getCell(pos.layer, pos.row, pos.col)
        if (existing != CellState.EMPTY) {
            return ValidationResult.Invalid(
                "Cell $pos is already occupied by $existing."
            )
        }

        return ValidationResult.Valid
    }
}