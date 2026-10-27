package com.sosclima.app.ui.alertas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosclima.app.data.repository.PreferenciasRepository
import com.sosclima.app.data.repository.PrevisaoRepository
import com.sosclima.app.domain.Cidade
import com.sosclima.app.domain.Cidades
import com.sosclima.app.domain.PrevisaoDia
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/** Os três estados obrigatórios de uma chamada de rede. */
sealed interface AlertasUiState {
    data object Loading : AlertasUiState
    data class Success(val cidade: Cidade, val dias: List<PrevisaoDia>) : AlertasUiState
    data class Error(val mensagem: String) : AlertasUiState
}

class AlertasViewModel(
    private val previsaoRepository: PrevisaoRepository,
    preferenciasRepository: PreferenciasRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AlertasUiState>(AlertasUiState.Loading)
    val uiState: StateFlow<AlertasUiState> = _uiState.asStateFlow()

    private var cidadeAtual: Cidade = Cidades.padrao
    private var carregamento: Job? = null

    init {
        // Recarrega sempre que a cidade escolhida nos Ajustes mudar.
        viewModelScope.launch {
            preferenciasRepository.preferencias
                .map { Cidades.porId(it.cidadeId) }
                .distinctUntilChanged()
                .collect { cidade ->
                    cidadeAtual = cidade
                    carregar()
                }
        }
    }

    fun tentarNovamente() = carregar()

    private fun carregar() {
        carregamento?.cancel()
        carregamento = viewModelScope.launch {
            _uiState.value = AlertasUiState.Loading
            _uiState.value = try {
                AlertasUiState.Success(cidadeAtual, previsaoRepository.buscarPrevisao(cidadeAtual))
            } catch (e: IOException) {
                AlertasUiState.Error("Sem conexão com a internet. Verifique sua rede e tente novamente.")
            } catch (e: HttpException) {
                AlertasUiState.Error("O serviço de previsão não respondeu (erro ${e.code()}). Tente novamente.")
            }
        }
    }
}
