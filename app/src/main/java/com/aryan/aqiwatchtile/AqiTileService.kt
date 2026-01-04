package com.aryan.aqiwatchtile

import android.content.Context
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.LayoutElementBuilders.*
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
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
    private val token = "YOUR_TOKEN_HERE"

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        return serviceScope.future {

            val prefs = getSharedPreferences("AQI_PREFS", MODE_PRIVATE)
            val lat = prefs.getFloat("LAT", 26.8467f).toDouble()
            val lon = prefs.getFloat("LON", 80.9462f).toDouble()
            val cityName = prefs.getString("CITY", "Setup") ?: "Setup"

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
        if (aqiValue == -1) {
            return Box.Builder().addContent(Text.Builder(context, "Err").build()).build()
        }

        val maxScale = 500f
        val progressRatio = (aqiValue / maxScale).coerceAtMost(1.0f)
        val aqiColor = getAqiColor(aqiValue)
        val statusText = getAqiLabel(aqiValue)

        val launchIntent = ActionBuilders.LaunchAction.Builder()
            .setAndroidActivity(
                ActionBuilders.AndroidActivity.Builder()
                    .setClassName(CitySetupActivity::class.java.name)
                    .setPackageName(context.packageName)
                    .build()
            ).build()
        val clickable = ModifiersBuilders.Clickable.Builder().setOnClick(launchIntent).build()

        val aqiMeter = CircularProgressIndicator.Builder()
            .setProgress(progressRatio)
            .setStartAngle(45f)
            .setEndAngle(315f)
            .setCircularProgressIndicatorColors(ProgressIndicatorColors(argb(aqiColor), argb(0xFF333333.toInt())))
            .build()

        val numberText = Text.Builder(context, aqiValue.toString())
            .setTypography(Typography.TYPOGRAPHY_DISPLAY1)
            .setColor(argb(aqiColor))
            .build()

        val statusLabel = Text.Builder(context, statusText)
            .setTypography(Typography.TYPOGRAPHY_CAPTION1)
            .setColor(argb(0xFFDDDDDD.toInt()))
            .build()

        val cityLabel = Text.Builder(context, cityName)
            .setTypography(Typography.TYPOGRAPHY_CAPTION2)
            .setColor(argb(0xFFAAAAAA.toInt()))
            .build()

        val textColumn = Column.Builder()
            .addContent(numberText)
            .addContent(statusLabel)
            .addContent(cityLabel)
            .build()

        return Box.Builder()
            .setModifiers(ModifiersBuilders.Modifiers.Builder().setClickable(clickable).build())
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