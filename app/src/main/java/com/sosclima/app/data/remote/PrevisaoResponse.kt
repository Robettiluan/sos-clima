package com.sosclima.app.data.remote

import com.google.gson.annotations.SerializedName
import com.sosclima.app.domain.PrevisaoDia
import java.time.LocalDate

/** DTO: espelha o JSON da Open-Meteo. Campos anuláveis porque a API pode omiti-los. */
data class PrevisaoResponse(
    @SerializedName("daily") val diario: DiarioDto?,
)

data class DiarioDto(
    @SerializedName("time") val datas: List<String>?,
    @SerializedName("weather_code") val codigos: List<Int?>?,
    @SerializedName("temperature_2m_max") val tempMax: List<Double?>?,
    @SerializedName("temperature_2m_min") val tempMin: List<Double?>?,
    @SerializedName("precipitation_sum") val chuva: List<Double?>?,
    @SerializedName("precipitation_probability_max") val probabilidade: List<Int?>?,
    @SerializedName("wind_speed_10m_max") val vento: List<Double?>?,
)

/** Converte o DTO da API no modelo de domínio (função de extensão). */
fun PrevisaoResponse.paraDominio(): List<PrevisaoDia> {
    val d = diario ?: return emptyList()
    return d.datas.orEmpty().mapIndexed { i, data ->
        PrevisaoDia(
            data = LocalDate.parse(data),
            codigoTempo = d.codigos?.getOrNull(i) ?: 0,
            tempMax = d.tempMax?.getOrNull(i) ?: 0.0,
            tempMin = d.tempMin?.getOrNull(i) ?: 0.0,
            chuvaMm = d.chuva?.getOrNull(i) ?: 0.0,
            probabilidadeChuva = d.probabilidade?.getOrNull(i) ?: 0,
            ventoKmh = d.vento?.getOrNull(i) ?: 0.0,
        )
    }
}
