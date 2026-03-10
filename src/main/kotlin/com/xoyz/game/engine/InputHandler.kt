package com.xoyz.game.engine

import com.xoyz.game.core.BoardPosition

/**
 * Converts raw screen-tap events into [BoardPosition] selections via ray-casting.
 *
 * Phase 2 implementation note:
 * ─────────────────────────────
 * With LibGDX: use `Ray` + `Intersector.intersectRayBounds()` to check each
 * cell's AABB against the ray emitted from the camera through the tap point.
 *
 * The resolved [BoardPosition] (or flat index) is forwarded to
 * [com.xoyz.game.ui.GameViewModel.applyMove].
 */
interface InputHandler {

    /**
     * Called when the user lifts their finger at ([screenX], [screenY]).
     *
     * @param screenX X in screen-space pixels (origin top-left).
     * @param screenY Y in screen-space pixels (origin top-left).
     * @return The [BoardPosition] that was tapped, or `null` if the tap
     *         did not hit any cell.
     */
    fun onTap(screenX: Float, screenY: Float): BoardPosition?

    /**
     * Called continuously while the user drags a finger.
     * Should update camera orbit via [CameraController.onDrag].
     */
    fun onDrag(screenX: Float, screenY: Float, deltaX: Float, deltaY: Float)

    /**
     * Called on a two-finger pinch gesture.
     * Should forward [scaleFactor] to [CameraController.onPinch].
     */
    fun onPinch(scaleFactor: Float)
}