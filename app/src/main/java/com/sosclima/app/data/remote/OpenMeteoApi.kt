package com.sosclima.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API pública Open-Meteo (https://open-meteo.com), gratuita e sem chave.
 * Exemplo: /v1/forecast?latitude=-23.55&longitude=-46.63&daily=...&forecast_days=7
 */
interface OpenMeteoApi {

    @GET("v1/forecast")
    suspend fun buscarPrevisao(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") campos: String = CAMPOS_DIARIOS,
        @Query("timezone") fusoHorario: String = "America/Sao_Paulo",
        @Query("forecast_days") dias: Int = 7,
    ): PrevisaoResponse

    companion object {
        const val BASE_URL = "https://api.open-meteo.com/"
        const val CAMPOS_DIARIOS = "weather_code,temperature_2m_max,temperature_2m_min," +
            "precipitation_sum,precipitation_probability_max,wind_speed_10m_max"
    }
}
