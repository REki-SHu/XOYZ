package com.xoyz.game.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xoyz.game.core.*
import com.xoyz.game.utils.XoyzLogger

/**
 * [ViewModel] that owns the [GameSession] for a local two-player game.
 *
 * The Activity / Fragment observes [gameSate] and [lastMoveResult] as
 * [LiveData]; it never manipulates [GameSession] directly.
 * This keeps the UI layer thin and the logic easily testable.
 */
class GameViewModel : ViewModel() {

    // ─── Backing fields ───────────────────────────────────────────────────────

    private val _gameState    = MutableLiveData<GameState>(GameState.InProgress)
    private val _board        = MutableLiveData<Board>()
    private val _currentPlayer= MutableLiveData<Player>()
    private val _lastMoveResult = MutableLiveData<MoveResult?>()
    private val _moveHistory  = MutableLiveData<List<Move>>(emptyList())

    // ─── Public LiveData ──────────────────────────────────────────────────────

    /** Emits the current [GameState] after every move. */
    val gameState: LiveData<GameState> = _gameState

    /** Emits the latest [Board] snapshot after every move. */
    val board: LiveData<Board> = _board

    /** Emits the player whose turn it currently is. */
    val currentPlayer: LiveData<Player> = _currentPlayer

    /** Emits the outcome of the most recent [applyMove] call. */
    val lastMoveResult: LiveData<MoveResult?> = _lastMoveResult

    /** Emits the full move history after every move. */
    val moveHistory: LiveData<List<Move>> = _moveHistory

    // ─── Session ──────────────────────────────────────────────────────────────

    private var session: GameSession = GameSession.newGame()
        set(value) {
            field = value
            _board.value         = value.board
            _currentPlayer.value = value.currentPlayer
            _gameState.value     = value.state
            _moveHistory.value   = value.history
        }

    init {
        // Emit initial board state
        session = session
    }

    // ─── Actions ──────────────────────────────────────────────────────────────

    /**
     * Starts a new game with optional custom player names.
     */
    fun startNewGame(player1Name: String = "Player 1", player2Name: String = "Player 2") {
        XoyzLogger.d("GameViewModel: startNewGame($player1Name, $player2Name)")
        session = GameSession.newGame(player1Name, player2Name)
        _lastMoveResult.value = null
    }

    /**
     * Attempts to apply a move at [flatIndex] using [symbol].
     *
     * The ViewModel resolves the flat index to a [BoardPosition] and constructs
     * a [Move] automatically — the UI only needs to pass the cell index and
     * the chosen symbol.
     *
     * @param flatIndex Cell index 0..26 (from a tap/ray-cast event).
     * @param symbol    The symbol the current player wants to place.
     */
    fun applyMove(flatIndex: Int, symbol: CellState) {
        val current = session.currentPlayer

        if (!current.owns(symbol)) {
            val result = MoveResult.Rejected(
                "${current.name} does not own symbol $symbol"
            )
            _lastMoveResult.value = result
            XoyzLogger.w("GameViewModel: rejected move — ${(result as MoveResult.Rejected).reason}")
            return
        }

        val position = try {
            BoardPosition.fromFlatIndex(flatIndex)
        } catch (e: IllegalArgumentException) {
            _lastMoveResult.value = MoveResult.Rejected("Invalid cell index: $flatIndex")
            return
        }

        val move   = Move(player = current, symbol = symbol, position = position)
        val result = session.applyMove(move)

        _lastMoveResult.value = result

        if (result is MoveResult.Accepted) {
            session = result.newSession
            XoyzLogger.d("GameViewModel: move accepted — $move, state=${session.state}")
        } else {
            XoyzLogger.w("GameViewModel: move rejected — ${(result as MoveResult.Rejected).reason}")
        }
    }
}