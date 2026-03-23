package com.xoyz.game.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xoyz.game.core.Board
import com.xoyz.game.core.GameSession
import com.xoyz.game.core.GameStatus

/**
 * ViewModel that owns the [GameSession] for a local two-player game.
 * The Activity observes [board], [status], [turnText], and [lastMoveValid].
 */
class GameViewModel : ViewModel() {

    private var session = GameSession()

    private val _board         = MutableLiveData<Board>(session.board)
    private val _status        = MutableLiveData<GameStatus>(session.status)
    private val _turnText      = MutableLiveData<String>(turnLabel())

    // Nullable Boolean: true = accepted, false = rejected, null = no move yet
    private val _lastMoveValid = MutableLiveData<Boolean?>()

    val board:         LiveData<Board>      = _board
    val status:        LiveData<GameStatus> = _status
    val turnText:      LiveData<String>     = _turnText
    val lastMoveValid: LiveData<Boolean?>   = _lastMoveValid

    // ── Actions ───────────────────────────────────────────────────────────────

    fun applyMove(layer: Int, row: Int, col: Int) {
        val accepted = session.makeMove(layer, row, col)
        _lastMoveValid.value = accepted
        if (accepted) {
            _board.value    = session.board
            _status.value   = session.status
            _turnText.value = turnLabel()
        }
    }

    fun startNewGame() {
        session = GameSession()
        _board.value         = session.board
        _status.value        = session.status
        _turnText.value      = turnLabel()
        _lastMoveValid.value = null   // now safe — type is Boolean?
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun turnLabel(): String {
        val playerNum = if (session.isPlayer1Turn) 1 else 2
        val symbols   = if (session.isPlayer1Turn) "X / Y" else "O / Z"
        return "Player $playerNum's turn  ($symbols)"
    }
}