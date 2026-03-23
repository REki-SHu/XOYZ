package com.xoyz.game.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.xoyz.game.R
import com.xoyz.game.core.CellState
import com.xoyz.game.core.GameSession
import com.xoyz.game.core.GameStatus

class GameActivity : AppCompatActivity() {

    private lateinit var session: GameSession
    private lateinit var cellButtons: Array<Array<Array<Button>>>
    private lateinit var tvTurnIndicator: TextView
    private var currentLayer = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_game)

        session = GameSession()
        tvTurnIndicator = findViewById(R.id.tvTurnIndicator)

        // Layer tab buttons
        findViewById<Button>(R.id.btnLayer0).setOnClickListener { switchLayer(0) }
        findViewById<Button>(R.id.btnLayer1).setOnClickListener { switchLayer(1) }
        findViewById<Button>(R.id.btnLayer2).setOnClickListener { switchLayer(2) }

        // Build 3 × 3 × 3 cell button grid
        cellButtons = Array(3) { layer ->
            Array(3) { row ->
                Array(3) { col ->
                    val id = resources.getIdentifier(
                        "cell_${layer}_${row}_${col}", "id", packageName
                    )
                    val btn = findViewById<Button>(id)
                    btn.setOnClickListener { onCellTapped(layer, row, col) }
                    btn
                }
            }
        }

        switchLayer(0)
        updateTurnIndicator()
    }

    private fun switchLayer(layer: Int) {
        currentLayer = layer
        val grids = arrayOf(
            findViewById<View>(R.id.gridLayer0),
            findViewById<View>(R.id.gridLayer1),
            findViewById<View>(R.id.gridLayer2)
        )
        grids.forEachIndexed { i, g ->
            g.visibility = if (i == layer) View.VISIBLE else View.GONE
        }
    }

    private fun onCellTapped(layer: Int, row: Int, col: Int) {
        val result = session.makeMove(layer, row, col)
        if (!result) {
            Toast.makeText(this, "Invalid move!", Toast.LENGTH_SHORT).show()
            return
        }

        val state = session.board.getCell(layer, row, col)
        cellButtons[layer][row][col].apply {
            text = state.symbol
            isEnabled = false
            // Colour-code each symbol
            setTextColor(symbolColor(state))
        }

        when (session.status) {
            GameStatus.X_WINS  -> showGameOver("X wins! 🎉")
            GameStatus.O_WINS  -> showGameOver("O wins! 🎉")
            GameStatus.Y_WINS  -> showGameOver("Y wins! 🎉")
            GameStatus.Z_WINS  -> showGameOver("Z wins! 🎉")
            GameStatus.DRAW    -> showGameOver("It's a Draw!")
            else               -> updateTurnIndicator()
        }
    }

    private fun updateTurnIndicator() {
        tvTurnIndicator.text = "${session.currentSymbol.symbol}'s turn"
    }

    private fun showGameOver(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage(message)
            .setPositiveButton("Play Again") { _, _ ->
                session = GameSession()
                resetBoard()
                updateTurnIndicator()
            }
            .setNegativeButton("Main Menu") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun resetBoard() {
        for (layer in 0..2)
            for (row in 0..2)
                for (col in 0..2) {
                    cellButtons[layer][row][col].text = ""
                    cellButtons[layer][row][col].isEnabled = true
                    cellButtons[layer][row][col].setTextColor(0xFFFFFFFF.toInt())
                }
    }

    /** Returns a distinct colour per symbol so players can tell them apart. */
    private fun symbolColor(state: CellState): Int = when (state) {
        CellState.X -> 0xFF1A73E8.toInt() // blue
        CellState.O -> 0xFFE53935.toInt() // red
        CellState.Y -> 0xFF43A047.toInt() // green
        CellState.Z -> 0xFFFFA000.toInt() // amber
        else        -> 0xFFFFFFFF.toInt() // white
    }
}