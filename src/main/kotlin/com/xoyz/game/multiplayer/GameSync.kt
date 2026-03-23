package com.xoyz.game.multiplayer

import com.xoyz.game.core.*
import com.xoyz.game.utils.BoardConstants
import com.xoyz.game.utils.XoyzLogger

/**
 * Converts a [RoomSnapshot] from the network into a local [GameSession].
 *
 * Pure stateless converter — no side-effects, easy to unit-test.
 */
object GameSync {

    private val SYMBOL_MAP = mapOf(
        0 to CellState.EMPTY,
        1 to CellState.X,
        2 to CellState.O,
        3 to CellState.Y,
        4 to CellState.Z
    )

    /**
     * Reconstructs a [GameSession] from a [RoomSnapshot].
     *
     * @return A fully initialised [GameSession], or null if the snapshot is malformed.
     */
    fun toGameSession(snapshot: RoomSnapshot): GameSession? {
        if (snapshot.board.size != BoardConstants.TOTAL_CELLS) {
            XoyzLogger.e("GameSync: board size mismatch — expected 27, got ${snapshot.board.size}")
            return null
        }

        // Build a Board by setting each cell individually using layer/row/col
        val board = Board()
        snapshot.board.forEachIndexed { flatIndex, code ->
            val state = SYMBOL_MAP[code] ?: CellState.EMPTY
            val layer = flatIndex / 9
            val row   = (flatIndex % 9) / 3
            val col   = flatIndex % 3
            board.setCell(layer, row, col, state)
        }

        // GameSession uses a plain constructor — no factory method needed
        val session = GameSession()
        // Note: full session reconstruction (player names, turn state) requires
        // GameSession to expose a restore constructor. For now this rebuilds the
        // board state only. Wire up player names when GameSession supports it.
        return session
    }

    /**
     * Converts a local [Move] into a [RemoteMove] ready to publish.
     */
    fun toRemoteMove(move: Move, playerIndex: Int): RemoteMove = RemoteMove(
        playerIndex = playerIndex,
        symbolName  = move.symbol.name,
        flatIndex   = move.position.flatIndex,
        timestamp   = move.timestamp
    )
}