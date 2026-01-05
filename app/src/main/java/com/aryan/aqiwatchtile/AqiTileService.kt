package com.aryan.aqiwatchtile

import android.content.Context
import androidx.wear.protolayout.*
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.LayoutElementBuilders.*
import androidx.wear.protolayout.material.CircularProgressIndicator
import androidx.wear.protolayout.material.ProgressIndicatorColors
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.future

class AqiTileService : TileService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    // YOUR WAQI TOKEN HERE
    private val token = "YOUR_token_HERE"

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        return serviceScope.future {

            val prefs = getSharedPreferences("AQI_PREFS", MODE_PRIVATE)
            val lat = prefs.getFloat("LAT", 26.8467f).toDouble()
            val lon = prefs.getFloat("LON", 80.9462f).toDouble()
            val cityName = prefs.getString("CITY", "Tap to Setup") ?: "Tap to Setup"

            var aqiValue: Int
            try {
                val response = WaqiClient.api.getRealAqi(lat, lon, token)
                aqiValue = if (response.status == "ok") {
                    response.data.aqi
                } else {
                    -1
                }
            } catch (e: Exception) {
                e.printStackTrace()
                aqiValue = -1
            }

            TileBuilders.Tile.Builder()
                .setResourcesVersion("1")
                .setFreshnessIntervalMillis(15 * 60 * 1000)
                .setTileTimeline(
                    TimelineBuilders.Timeline.Builder()
                        .addTimelineEntry(
                            TimelineBuilders.TimelineEntry.Builder()
                                .setLayout(
                                    Layout.Builder()
                                        .setRoot(buildLayout(aqiValue, cityName, applicationContext))
                                        .build()
                                )
                                .build()
                        ).build()
                ).build()
        }
    }

    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> {
        return serviceScope.future { ResourceBuilders.Resources.Builder().setVersion("1").build() }
    }

    private fun buildLayout(aqiValue: Int, cityName: String, context: Context): LayoutElement {
        val maxScale = 500f
        val progressRatio = (aqiValue / maxScale).coerceAtMost(1.0f)
        val aqiColor = if (aqiValue == -1) 0xFFAAAAAA.toInt() else getAqiColor(aqiValue)
        val statusText = if (aqiValue == -1) "Error" else getAqiLabel(aqiValue)
        val displayText = if (aqiValue == -1) "--" else aqiValue.toString()

        val refreshAction = ActionBuilders.LoadAction.Builder().build()
        val refreshClickable = ModifiersBuilders.Clickable.Builder()
            .setOnClick(refreshAction)
            .build()

        val setupIntent = ActionBuilders.LaunchAction.Builder()
            .setAndroidActivity(
                ActionBuilders.AndroidActivity.Builder()
                    .setClassName(CitySetupActivity::class.java.name)
                    .setPackageName(context.packageName)
                    .build()
            ).build()
        val setupClickable = ModifiersBuilders.Clickable.Builder()
            .setOnClick(setupIntent)
            .build()

        val aqiMeter = CircularProgressIndicator.Builder()
            .setProgress(progressRatio)
            .setStartAngle(45f)
            .setEndAngle(315f)
            .setCircularProgressIndicatorColors(ProgressIndicatorColors(argb(aqiColor), argb(0xFF333333.toInt())))
            .build()

        val numberText = Text.Builder(context, displayText)
            .setTypography(Typography.TYPOGRAPHY_DISPLAY1)
            .setColor(argb(aqiColor))
            .build()

        val statusLabel = Text.Builder(context, statusText)
            .setTypography(Typography.TYPOGRAPHY_CAPTION1)
            .setColor(argb(0xFFDDDDDD.toInt()))
            .build()

        val cityButton = Text.Builder(context, cityName)
            .setTypography(Typography.TYPOGRAPHY_CAPTION2)
            .setColor(argb(0xFFAAAAAA.toInt()))
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setClickable(setupClickable)
                    .build()
            )
            .build()

        val refreshBtn = Text.Builder(context, "↻ Sync")
            .setTypography(Typography.TYPOGRAPHY_CAPTION2)
            .setColor(argb(0xFF00E5FF.toInt()))
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setClickable(refreshClickable)
                    .build()
            )
            .build()

        val textColumn = Column.Builder()
            .addContent(numberText)
            .addContent(statusLabel)
            .addContent(cityButton)
            .addContent(Spacer.Builder().setHeight(DimensionBuilders.dp(8f)).build())
            .addContent(refreshBtn)
            .build()

        return Box.Builder()
            .addContent(aqiMeter)
            .addContent(textColumn)
            .build()
    }

    private fun getAqiColor(aqi: Int): Int {
        return when {
            aqi <= 50 -> 0xFF00E400.toInt()
            aqi <= 100 -> 0xFFFFFF00.toInt()
            aqi <= 200 -> 0xFFFF7E00.toInt()
            aqi <= 300 -> 0xFFFF0000.toInt()
            aqi <= 400 -> 0xFF8F3F97.toInt()
            else -> 0xFF7E0023.toInt()
        }
    }

    private fun getAqiLabel(aqi: Int): String {
        return when {
            aqi <= 50 -> "Good"
            aqi <= 100 -> "Satisfactory"
            aqi <= 200 -> "Moderate"
            aqi <= 300 -> "Poor"
            aqi <= 400 -> "Very Poor"
            else -> "Severe"
        }
    }
}