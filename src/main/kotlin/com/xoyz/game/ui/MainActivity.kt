package com.xoyz.game.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.xoyz.game.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnLocalGame).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        findViewById<Button>(R.id.btnMultiplayer).setOnClickListener {
            // TODO: Phase 4 — multiplayer lobby
        }

        findViewById<Button>(R.id.btnRules).setOnClickListener {
            // TODO: show rules dialog
        }
    }
}