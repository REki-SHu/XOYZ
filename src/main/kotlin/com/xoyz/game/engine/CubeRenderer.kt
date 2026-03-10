package com.xoyz.game.engine

import com.xoyz.game.core.Board

/**
 * Contract for the 3D cube rendering surface.
 *
 * Phase 2 implementation note:
 * ─────────────────────────────
 * Implement this interface with a LibGDX `ApplicationAdapter` (or a raw
 * `GLSurfaceView.Renderer` if going with bare OpenGL ES).
 *
 * Responsibilities:
 *  - Draw the 3×3×3 wireframe cube grid.
 *  - Render occupied cells with the appropriate symbol (X / O / Y / Z).
 *  - Highlight the currently hovered or selected cell.
 *  - Animate symbol placements and winning-line glows.
 */
interface CubeRenderer {

    /**
     * Initialise GPU resources (shaders, meshes, textures).
     * Called once when the GL surface is created.
     */
    fun onCreate()

    /**
     * Re-initialise after a surface resize (orientation change, etc.).
     *
     * @param width  New surface width in pixels.
     * @param height New surface height in pixels.
     */
    fun onResize(width: Int, height: Int)

    /**
     * Draw one frame.  Called at the display refresh rate (typically 60 Hz).
     *
     * @param board   The current board snapshot to render.
     * @param deltaMs Milliseconds elapsed since the previous frame.
     */
    fun onFrame(board: Board, deltaMs: Float)

    /**
     * Highlights [flatIndex] as the currently selected cell.
     * Pass -1 to clear the highlight.
     */
    fun setHighlight(flatIndex: Int)

    /**
     * Triggers the win-line glow animation for the given flat indices.
     *
     * @param lineIndices Three flat indices (0..26) forming the winning line.
     */
    fun playWinAnimation(lineIndices: IntArray)

    /** Releases GPU resources. Called when the GL surface is destroyed. */
    fun onDestroy()
}