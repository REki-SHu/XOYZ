package com.xoyz.game.multiplayer

import com.xoyz.game.utils.XoyzLogger

/**
 * Manages the network connection lifecycle.
 *
 * This class is intentionally left as a stub.  When Firebase RTDB or a
 * custom WebSocket backend is integrated, this is where you:
 *  - Initialise the Firebase App / WebSocket client
 *  - Handle anonymous authentication
 *  - Reconnect after network interruptions
 *  - Tear down connections cleanly when the user leaves the game
 *
 * The rest of the app interacts with [ConnectionManager] only through
 * [RoomManager], keeping the connection strategy swappable.
 */
class ConnectionManager {

    /** Observable connection state. */
    enum class State { DISCONNECTED, CONNECTING, CONNECTED, ERROR }

    private var _state: State = State.DISCONNECTED
    val state: State get() = _state

    /**
     * Initiates the connection.
     * Replace the body with real Firebase / WebSocket init code.
     */
    fun connect(onConnected: () -> Unit, onError: (Throwable) -> Unit) {
        XoyzLogger.d("ConnectionManager: connect() called — stub, no-op for now")
        _state = State.CONNECTED
        onConnected()
    }

    /** Tears down the connection. */
    fun disconnect() {
        XoyzLogger.d("ConnectionManager: disconnect() called")
        _state = State.DISCONNECTED
    }
}