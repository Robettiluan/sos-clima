package com.sosclima.app.ui.alertas

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosclima.app.data.repository.PrevisaoRepository
import com.sosclima.app.domain.PrevisaoDia
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DetalheAlertaViewModel(
    savedStateHandle: SavedStateHandle,
    previsaoRepository: PrevisaoRepository,
) : ViewModel() {

    /** Data recebida como argumento de navegação (ex.: "2026-10-21"). */
    private val data: String = checkNotNull(savedStateHandle[ARG_DATA])

    val dia: StateFlow<PrevisaoDia?> = previsaoRepository.ultimaPrevisao
        .map { dias -> dias.firstOrNull { it.data.toString() == data } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    companion object {
        const val ARG_DATA = "data"
    }
}
