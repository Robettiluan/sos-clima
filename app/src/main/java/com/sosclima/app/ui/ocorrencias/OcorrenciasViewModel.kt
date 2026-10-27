package com.sosclima.app.ui.ocorrencias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosclima.app.data.local.Ocorrencia
import com.sosclima.app.data.repository.OcorrenciaRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FiltroOcorrencia(val rotulo: String) {
    TODAS("Todas"),
    ABERTAS("Abertas"),
    RESOLVIDAS("Resolvidas"),
}

/** Qual diálogo de confirmação está aberto (no máximo um por vez). */
sealed interface DialogoOcorrencias {
    data object Nenhum : DialogoOcorrencias
    data class ConfirmarExclusao(val ocorrencia: Ocorrencia) : DialogoOcorrencias
    data class ConfirmarLimpeza(val quantidade: Int) : DialogoOcorrencias
}

data class OcorrenciasUiState(
    val itens: List<Ocorrencia> = emptyList(),
    val filtro: FiltroOcorrencia = FiltroOcorrencia.TODAS,
    val totalResolvidas: Int = 0,
    val carregando: Boolean = true,
    val dialogo: DialogoOcorrencias = DialogoOcorrencias.Nenhum,
)

fun List<Ocorrencia>.filtrarPor(filtro: FiltroOcorrencia): List<Ocorrencia> = when (filtro) {
    FiltroOcorrencia.TODAS -> this
    FiltroOcorrencia.ABERTAS -> filterNot { it.resolvida }
    FiltroOcorrencia.RESOLVIDAS -> filter { it.resolvida }
}

class OcorrenciasViewModel(
    private val repository: OcorrenciaRepository,
) : ViewModel() {

    private val filtro = MutableStateFlow(FiltroOcorrencia.TODAS)
    private val dialogo = MutableStateFlow<DialogoOcorrencias>(DialogoOcorrencias.Nenhum)

    /** Mensagens únicas (Snackbar), que não devem reaparecer ao girar a tela. */
    private val _mensagens = MutableSharedFlow<String>()
    val mensagens: SharedFlow<String> = _mensagens.asSharedFlow()

    val uiState: StateFlow<OcorrenciasUiState> =
        combine(repository.observarTodas(), filtro, dialogo) { lista, filtroAtual, dialogoAtual ->
            OcorrenciasUiState(
                itens = lista.filtrarPor(filtroAtual),
                filtro = filtroAtual,
                totalResolvidas = lista.count { it.resolvida },
                carregando = false,
                dialogo = dialogoAtual,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), OcorrenciasUiState())

    fun selecionarFiltro(novo: FiltroOcorrencia) {
        filtro.value = novo
    }

    fun alternarResolvida(ocorrencia: Ocorrencia) {
        viewModelScope.launch { repository.alternarResolvida(ocorrencia) }
    }

    fun solicitarExclusao(ocorrencia: Ocorrencia) {
        dialogo.value = DialogoOcorrencias.ConfirmarExclusao(ocorrencia)
    }

    fun solicitarLimpeza() {
        val quantidade = uiState.value.totalResolvidas
        if (quantidade > 0) dialogo.value = DialogoOcorrencias.ConfirmarLimpeza(quantidade)
    }

    fun confirmarDialogo() {
        val atual = dialogo.value
        dialogo.value = DialogoOcorrencias.Nenhum
        viewModelScope.launch {
            when (atual) {
                is DialogoOcorrencias.ConfirmarExclusao -> {
                    repository.excluir(atual.ocorrencia)
                    _mensagens.emit("Ocorrência excluída")
                }
                is DialogoOcorrencias.ConfirmarLimpeza -> {
                    val removidas = repository.excluirResolvidas()
                    _mensagens.emit("$removidas ocorrência(s) resolvida(s) removida(s)")
                }
                DialogoOcorrencias.Nenhum -> Unit
            }
        }
    }

    fun fecharDialogo() {
        dialogo.value = DialogoOcorrencias.Nenhum
    }
}
