package com.xoyz.game.multiplayer

/**
 * Contract for creating and joining multiplayer rooms.
 *
 * Implementations can back this with Firebase RTDB, a custom WebSocket
 * server, or a local in-memory store for testing — the rest of the app
 * never needs to know which one is active.
 */
interface RoomManager {

    /**
     * Creates a new room and returns its [RoomSnapshot].
     *
     * @param hostName Display name for the creating player.
     * @param onResult Callback: [Result.success] with the [RoomSnapshot],
     *                 or [Result.failure] with an exception.
     */
    fun createRoom(hostName: String, onResult: (Result<RoomSnapshot>) -> Unit)

    /**
     * Joins an existing room identified by [roomCode].
     *
     * @param roomCode  6-character alphanumeric code (case-insensitive).
     * @param guestName Display name for the joining player.
     * @param onResult  Callback with the updated [RoomSnapshot] or an error.
     */
    fun joinRoom(
        roomCode: String,
        guestName: String,
        onResult: (Result<RoomSnapshot>) -> Unit
    )

    /**
     * Subscribes to real-time updates for [roomCode].
     *
     * [onUpdate] is called every time the room state changes in the DB.
     * Call the returned [Unsubscribe] lambda to stop listening.
     */
    fun observeRoom(roomCode: String, onUpdate: (RoomSnapshot) -> Unit): Unsubscribe

    /**
     * Publishes a move to the room.
     *
     * @param roomCode   The room to update.
     * @param move       The move to broadcast.
     * @param onResult   Callback confirming success or failure.
     */
    fun sendMove(
        roomCode: String,
        move: RemoteMove,
        onResult: (Result<Unit>) -> Unit
    )

    /**
     * Closes the room and cleans up remote state.
     */
    fun closeRoom(roomCode: String, onResult: (Result<Unit>) -> Unit)
}

/** A no-argument lambda that cancels a listener subscription when invoked. */
typealias Unsubscribe = () -> Unit