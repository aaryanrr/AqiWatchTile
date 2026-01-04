package com.aryan.aqiwatchtile

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.wear.tiles.TileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CitySetupActivity : Activity() {

    // YOUR OPENWEATHER TOKEN HERE
    private val key = "YOUR_TOKEN_HERE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
            gravity = android.view.Gravity.CENTER
        }

        val title = TextView(this).apply { text = "Enter City Name"; textSize = 16f }
        val input = EditText(this).apply { hint = "e.g. Lucknow"; textSize = 14f }
        val btnSave = Button(this).apply { text = "Save & Sync" }
        val status = TextView(this).apply { text = ""; textSize = 12f }

        layout.addView(title)
        layout.addView(input)
        layout.addView(btnSave)
        layout.addView(status)

        setContentView(layout)

        btnSave.setOnClickListener {
            val cityName = input.text.toString()
            if (cityName.isNotEmpty()) {
                status.text = "Searching..."
                fetchCoordinates(cityName, status)
            }
        }
    }

    private fun fetchCoordinates(city: String, statusView: TextView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val results = NetworkClient.api.getLatLon(city, 1, key)

                if (results.isNotEmpty()) {
                    val location = results[0]

                    val prefs = getSharedPreferences("AQI_PREFS", MODE_PRIVATE)
                    with(prefs.edit()) {
                        putFloat("LAT", location.lat.toFloat())
                        putFloat("LON", location.lon.toFloat())
                        putString("CITY", location.name)
                        putBoolean("IS_MANUAL", true)
                        apply()
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CitySetupActivity, "Saved: ${location.name}", Toast.LENGTH_SHORT).show()
                        TileService.getUpdater(this@CitySetupActivity)
                            .requestUpdate(AqiTileService::class.java)
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) { statusView.text = "City not found!" }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { statusView.text = "Net Error: Check WiFi" }
            }
        }
    }
}