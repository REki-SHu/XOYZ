package com.xoyz.game.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.xoyz.game.R
import com.xoyz.game.core.GameState
import com.xoyz.game.core.MoveResult
import com.xoyz.game.utils.XoyzLogger

/**
 * Hosts a local two-player XOYZ game session.
 *
 * This Activity is deliberately thin — it:
 *  1. Observes [GameViewModel] for state changes.
 *  2. Updates the HUD (player label, board).
 *  3. Shows win/draw dialogs.
 *
 * The 3D rendering surface (LibGDX / OpenGL) will be attached here in
 * Phase 2; for now the activity acts as a skeleton/integration point.
 */
class LocalGameActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_game)
        XoyzLogger.i("LocalGameActivity: onCreate")

        observeViewModel()

        viewModel.startNewGame(
            player1Name = intent.getStringExtra(EXTRA_P1_NAME) ?: "Player 1",
            player2Name = intent.getStringExtra(EXTRA_P2_NAME) ?: "Player 2"
        )
    }

    // ─── Observers ────────────────────────────────────────────────────────────

    private fun observeViewModel() {
        viewModel.gameState.observe(this) { state ->
            when (state) {
                is GameState.Won  -> showWinDialog(state.winner.name)
                is GameState.Draw -> showDrawDialog()
                else              -> { /* board updated via board LiveData */ }
            }
        }

        viewModel.lastMoveResult.observe(this) { result ->
            if (result is MoveResult.Rejected) {
                Toast.makeText(this, result.reason, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.currentPlayer.observe(this) { player ->
            XoyzLogger.d("LocalGameActivity: current player → ${player.name}")
            // TODO: update HUD label
        }
    }

    // ─── Dialogs ──────────────────────────────────────────────────────────────

    private fun showWinDialog(winnerName: String) {
        AlertDialog.Builder(this)
            .setTitle("🎉 $winnerName wins!")
            .setMessage("Congratulations!")
            .setPositiveButton("Play Again") { _, _ -> viewModel.startNewGame() }
            .setNegativeButton("Main Menu")  { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun showDrawDialog() {
        AlertDialog.Builder(this)
            .setTitle("It's a Draw!")
            .setMessage("No more moves available.")
            .setPositiveButton("Play Again") { _, _ -> viewModel.startNewGame() }
            .setNegativeButton("Main Menu")  { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    // ─── Constants ────────────────────────────────────────────────────────────

    companion object {
        const val EXTRA_P1_NAME = "extra_player1_name"
        const val EXTRA_P2_NAME = "extra_player2_name"
    }
}