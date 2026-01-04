package com.aryan.aqiwatchtile

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class GeoResponse(val name: String, val lat: Double, val lon: Double)

interface WeatherApiService {
    @GET("geo/1.0/direct")
    suspend fun getLatLon(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String
    ): List<GeoResponse>
}

object NetworkClient {
    val api: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}

data class WaqiResponse(val status: String, val data: WaqiData)
data class WaqiData(val aqi: Int)

interface WaqiApiService {
    @GET("feed/geo:{lat};{lon}/")
    suspend fun getRealAqi(
        @Path("lat") lat: Double,
        @Path("lon") lon: Double,
        @Query("token") token: String
    ): WaqiResponse
}

object WaqiClient {
    val api: WaqiApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.waqi.info/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WaqiApiService::class.java)
    }
}