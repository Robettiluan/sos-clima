package com.sosclima.app.di

import android.content.Context
import com.sosclima.app.data.repository.PreferenciasRepository
import com.sosclima.app.data.local.SosClimaDatabase
import com.sosclima.app.data.local.dataStore
import com.sosclima.app.data.remote.OpenMeteoApi
import com.sosclima.app.data.repository.OcorrenciaRepository
import com.sosclima.app.data.repository.PrevisaoRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Injeção de dependência manual. O Context fica só aqui (para criar o banco e o DataStore);
 * os ViewModels recebem apenas os repositórios.
 */
class AppContainer(context: Context) {

    private val retrofit: Retrofit by lazy {
        val cliente = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            .build()

        Retrofit.Builder()
            .baseUrl(OpenMeteoApi.BASE_URL)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val previsaoRepository: PrevisaoRepository by lazy {
        PrevisaoRepository(retrofit.create(OpenMeteoApi::class.java))
    }

    val ocorrenciaRepository: OcorrenciaRepository by lazy {
        OcorrenciaRepository(SosClimaDatabase.getInstance(context).ocorrenciaDao())
    }

    val preferenciasRepository: PreferenciasRepository by lazy {
        PreferenciasRepository(context.dataStore)
    }
}
