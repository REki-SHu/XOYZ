package com.xoyz.game.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xoyz.game.R
import com.xoyz.game.utils.XoyzLogger

/**
 * Application entry point.
 *
 * Responsibilities:
 *  - Show the main menu on first launch.
 *  - Act as the top-level navigation host.
 *
 * All game logic is intentionally absent here — this activity only
 * decides *which* screen to show next.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        XoyzLogger.i("MainActivity: onCreate")
    }

    /** Called when the user taps "Local Game" on the menu screen. */
    fun onLocalGameClicked() {
        XoyzLogger.d("MainActivity: navigating to LocalGameActivity")
        val intent = Intent(this, LocalGameActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps "Multiplayer" — future: navigate to lobby. */
    fun onMultiplayerClicked() {
        XoyzLogger.d("MainActivity: multiplayer not yet implemented")
        // TODO: startActivity(Intent(this, LobbyActivity::class.java))
    }
}