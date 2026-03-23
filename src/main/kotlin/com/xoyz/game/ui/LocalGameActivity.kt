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
    private lateinit var tvLayerLabel: Array<TextView>
    private var currentLayer = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_game)

        session = GameSession()

        tvTurnIndicator = findViewById(R.id.tvTurnIndicator)

        // Layer tab buttons
        val btnLayer0 = findViewById<Button>(R.id.btnLayer0)
        val btnLayer1 = findViewById<Button>(R.id.btnLayer1)
        val btnLayer2 = findViewById<Button>(R.id.btnLayer2)

        btnLayer0.setOnClickListener { switchLayer(0) }
        btnLayer1.setOnClickListener { switchLayer(1) }
        btnLayer2.setOnClickListener { switchLayer(2) }

        // Build 3 layers × 3×3 grids of buttons
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
        grids.forEachIndexed { i, g -> g.visibility = if (i == layer) View.VISIBLE else View.GONE }
    }

    private fun onCellTapped(layer: Int, row: Int, col: Int) {
        val result = session.makeMove(layer, row, col)
        if (!result) {
            Toast.makeText(this, "Invalid move!", Toast.LENGTH_SHORT).show()
            return
        }

        // Update button text
        val state = session.board.getCell(layer, row, col)
        cellButtons[layer][row][col].text = state.symbol
        cellButtons[layer][row][col].isEnabled = false

        when (session.status) {
            GameStatus.PLAYER1_WINS -> showGameOver("Player 1 Wins! 🎉")
            GameStatus.PLAYER2_WINS -> showGameOver("Player 2 Wins! 🎉")
            GameStatus.DRAW -> showGameOver("It's a Draw!")
            else -> updateTurnIndicator()
        }
    }

    private fun updateTurnIndicator() {
        val playerNum = if (session.isPlayer1Turn) 1 else 2
        val symbols = if (session.isPlayer1Turn) "X / Y" else "O / Z"
        tvTurnIndicator.text = "Player $playerNum's turn  ($symbols)"
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
                }
    }
}