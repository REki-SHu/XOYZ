package com.xoyz.game.multiplayer

import com.xoyz.game.core.CellState

/**
 * Data transfer objects shared between the local client and Firebase / WebSocket.
 *
 * All classes are plain data classes so they can be serialised with any
 * JSON library (Gson, kotlinx.serialization, Firebase SDK default mapper).
 */

/** Possible room lifecycle states as stored in the remote DB. */
enum class RoomStatus { WAITING, IN_PROGRESS, FINISHED }

/**
 * Minimal information about a player stored inside a [RoomSnapshot].
 *
 * @property uid     Unique ID (Firebase anonymous UID, or a UUID for WebSocket).
 * @property name    Display name.
 * @property ready   Whether this player has signalled they are ready to start.
 */
data class RemotePlayerInfo(
    val uid: String  = "",
    val name: String = "",
    val ready: Boolean = false
)

/**
 * A single move transmitted over the network.
 *
 * Indices are ints so they serialise cleanly without custom adapters.
 *
 * @property playerIndex 0 = player1, 1 = player2.
 * @property symbolName  [CellState] enum name ("X", "O", "Y", "Z").
 * @property flatIndex   Cell index 0..26.
 * @property timestamp   Epoch millis.
 */
data class RemoteMove(
    val playerIndex: Int    = 0,
    val symbolName: String  = "",
    val flatIndex: Int      = 0,
    val timestamp: Long     = 0L
) {
    /** Convenience: converts [symbolName] back to a [CellState]. */
    val symbol: CellState get() = CellState.valueOf(symbolName)
}

/**
 * Complete room state as persisted in the remote database.
 *
 * @property code        6-character alphanumeric room code.
 * @property status      Current room lifecycle status.
 * @property player1     Host player info.
 * @property player2     Guest player info (null until joined).
 * @property board       Flat 27-element board as Int (0=EMPTY, 1=X, 2=O, 3=Y, 4=Z).
 * @property currentTurn 1 or 2.
 * @property moves       Ordered list of moves made so far.
 * @property winnerIndex 1, 2, or 0 if no winner yet.
 * @property createdAt   Epoch millis when the room was created.
 */
data class RoomSnapshot(
    val code: String              = "",
    val status: RoomStatus        = RoomStatus.WAITING,
    val player1: RemotePlayerInfo = RemotePlayerInfo(),
    val player2: RemotePlayerInfo? = null,
    val board: List<Int>          = List(27) { 0 },
    val currentTurn: Int          = 1,
    val moves: List<RemoteMove>   = emptyList(),
    val winnerIndex: Int          = 0,
    val createdAt: Long           = System.currentTimeMillis()
)