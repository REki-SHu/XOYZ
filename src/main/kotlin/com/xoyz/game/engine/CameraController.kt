package com.xoyz.game.engine

/**
 * Controls the orbital camera around the XOYZ cube.
 *
 * Phase 2 implementation note:
 * ─────────────────────────────
 * When LibGDX is integrated, this class should wrap a `PerspectiveCamera`
 * and a `CameraInputController` (or a custom gesture detector).
 *
 * Responsibilities:
 *  - Rotate the camera around the cube when the user swipes.
 *  - Zoom in/out on pinch gesture.
 *  - Snap to predefined angles (isometric, top-view, side-view) on double-tap.
 *  - Provide the current view/projection matrices to [CubeRenderer].
 *
 * This stub establishes the API contract so the rest of the engine
 * can be written against it before LibGDX is wired in.
 */
interface CameraController {

    /**
     * Processes a drag event (finger moved [deltaX], [deltaY] pixels).
     * Converts pixel deltas into orbit angles.
     */
    fun onDrag(deltaX: Float, deltaY: Float)

    /**
     * Processes a pinch event.
     * [scaleFactor] > 1 means pinch-open (zoom in), < 1 means pinch-close.
     */
    fun onPinch(scaleFactor: Float)

    /**
     * Snaps the camera to the given [preset] angle.
     * @param preset One of the named viewpoints (ISOMETRIC, TOP, FRONT, RIGHT).
     */
    fun snapTo(preset: CameraPreset)

    /** Resets camera to the default isometric position. */
    fun reset()
}

/** Named camera presets for snap-to-angle functionality. */
enum class CameraPreset { ISOMETRIC, TOP, FRONT, RIGHT }