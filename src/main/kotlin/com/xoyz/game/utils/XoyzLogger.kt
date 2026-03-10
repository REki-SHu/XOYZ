package com.xoyz.game.utils

import android.util.Log

/**
 * Thin wrapper around Android's [Log] class.
 *
 * Centralising log calls here means:
 *  - The log tag is consistent across the whole app.
 *  - Debug calls can be stripped in release builds by setting [debugEnabled].
 *  - Unit tests can compile and run without Android runtime (just no output).
 */
object XoyzLogger {

    private const val TAG = "XOYZ"

    /** Set to false to suppress verbose/debug output in release builds. */
    var debugEnabled: Boolean = true

    fun v(message: String) {
        if (debugEnabled) safeLog { Log.v(TAG, message) }
    }

    fun d(message: String) {
        if (debugEnabled) safeLog { Log.d(TAG, message) }
    }

    fun i(message: String) = safeLog { Log.i(TAG, message) }

    fun w(message: String, throwable: Throwable? = null) =
        safeLog { Log.w(TAG, message, throwable) }

    fun e(message: String, throwable: Throwable? = null) =
        safeLog { Log.e(TAG, message, throwable) }

    /**
     * Executes [block] and swallows [RuntimeException] so log calls never
     * crash the app (e.g. if called from a JVM-only test environment).
     */
    private inline fun safeLog(block: () -> Unit) {
        try { block() } catch (_: RuntimeException) { /* no-op in JVM tests */ }
    }
}