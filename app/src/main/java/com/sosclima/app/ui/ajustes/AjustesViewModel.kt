package com.sosclima.app.ui.ajustes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosclima.app.data.repository.PreferenciasRepository
import com.sosclima.app.domain.Cidade
import com.sosclima.app.domain.Cidades
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AjustesUiState(
    val temaEscuro: Boolean = false,
    val cidade: Cidade = Cidades.padrao,
    val cidadesDisponiveis: List<Cidade> = Cidades.todas,
)

class AjustesViewModel(
    private val preferenciasRepository: PreferenciasRepository,
) : ViewModel() {

    val uiState: StateFlow<AjustesUiState> = preferenciasRepository.preferencias
        .map { AjustesUiState(temaEscuro = it.temaEscuro, cidade = Cidades.porId(it.cidadeId)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AjustesUiState())

    fun alterarTemaEscuro(ativo: Boolean) {
        viewModelScope.launch { preferenciasRepository.salvarTemaEscuro(ativo) }
    }

    fun selecionarCidade(cidade: Cidade) {
        viewModelScope.launch { preferenciasRepository.salvarCidade(cidade.id) }
    }
}
