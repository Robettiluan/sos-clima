package com.sosclima.app.data.repository

import com.sosclima.app.data.remote.OpenMeteoApi
import com.sosclima.app.data.remote.paraDominio
import com.sosclima.app.domain.Cidade
import com.sosclima.app.domain.PrevisaoDia
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class PrevisaoRepository(
    private val api: OpenMeteoApi,
    private val io: CoroutineDispatcher = Dispatchers.IO,
) {
    /** Última previsão carregada, usada pela tela de detalhes sem nova chamada de rede. */
    private val _ultimaPrevisao = MutableStateFlow<List<PrevisaoDia>>(emptyList())
    val ultimaPrevisao: StateFlow<List<PrevisaoDia>> = _ultimaPrevisao.asStateFlow()

    suspend fun buscarPrevisao(cidade: Cidade): List<PrevisaoDia> {
        val dias = withContext(io) {
            api.buscarPrevisao(cidade.latitude, cidade.longitude).paraDominio()
        }
        _ultimaPrevisao.value = dias
        return dias
    }
}
