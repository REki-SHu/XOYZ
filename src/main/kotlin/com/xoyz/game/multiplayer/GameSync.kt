package com.xoyz.game.multiplayer

import com.xoyz.game.core.*
import com.xoyz.game.utils.BoardConstants
import com.xoyz.game.utils.XoyzLogger

/**
 * Converts a [RoomSnapshot] from the network into a local [GameSession].
 *
 * [GameSync] is a pure stateless converter — it has no side-effects and holds
 * no mutable state, which makes it straightforward to unit-test.
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
     * @param snapshot The authoritative room state received from the server.
     * @return A fully initialised [GameSession], or null if the snapshot is malformed.
     */
    fun toGameSession(snapshot: RoomSnapshot): GameSession? {
        if (snapshot.board.size != BoardConstants.TOTAL_CELLS) {
            XoyzLogger.e("GameSync: board size mismatch — expected 27, got ${snapshot.board.size}")
            return null
        }

        val player1 = Player.player1(snapshot.player1.name.ifBlank { "Player 1" })
        val player2 = Player.player2(
            snapshot.player2?.name?.ifBlank { "Player 2" } ?: "Player 2"
        )

        val cells = Array(BoardConstants.TOTAL_CELLS) { idx ->
            SYMBOL_MAP[snapshot.board[idx]] ?: CellState.EMPTY
        }
        val board = Board.fromArray(cells)

        return GameSession.create(
            player1 = player1,
            player2 = player2,
            board   = board
        )
    }

    /**
     * Converts a local [Move] into a [RemoteMove] ready to publish.
     *
     * @param move        The local move to serialise.
     * @param playerIndex 0 for player1, 1 for player2.
     */
    fun toRemoteMove(move: Move, playerIndex: Int): RemoteMove = RemoteMove(
        playerIndex = playerIndex,
        symbolName  = move.symbol.name,
        flatIndex   = move.position.flatIndex,
        timestamp   = move.timestamp
    )
}