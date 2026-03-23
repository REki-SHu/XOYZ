package com.xoyz.game.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xoyz.game.core.Board
import com.xoyz.game.core.GameSession
import com.xoyz.game.core.GameStatus

/**
 * ViewModel for a local XOYZ game.
 * Turn order: X → O → Y → Z → X …
 * Win: 3 of the same symbol in any line.
 */
class GameViewModel : ViewModel() {

    private var session = GameSession()

    private val _board         = MutableLiveData<Board>(session.board)
    private val _status        = MutableLiveData<GameStatus>(session.status)
    private val _turnText      = MutableLiveData<String>(turnLabel())
    private val _lastMoveValid = MutableLiveData<Boolean?>()

    val board:         LiveData<Board>      = _board
    val status:        LiveData<GameStatus> = _status
    val turnText:      LiveData<String>     = _turnText
    val lastMoveValid: LiveData<Boolean?>   = _lastMoveValid

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
        session              = GameSession()
        _board.value         = session.board
        _status.value        = session.status
        _turnText.value      = turnLabel()
        _lastMoveValid.value = null
    }

    private fun turnLabel(): String =
        "${session.currentSymbol.symbol}'s turn"
}