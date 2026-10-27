package com.sosclima.app.ui.ocorrencias

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosclima.app.data.local.Gravidade
import com.sosclima.app.data.local.Ocorrencia
import com.sosclima.app.data.local.TipoOcorrencia
import com.sosclima.app.data.repository.OcorrenciaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Campos editáveis do formulário. */
data class FormularioOcorrencia(
    val titulo: String = "",
    val endereco: String = "",
    val descricao: String = "",
    val tipo: TipoOcorrencia = TipoOcorrencia.ALAGAMENTO,
    val gravidade: Gravidade = Gravidade.MEDIA,
    val resolvida: Boolean = false,
)

data class FormUiState(
    val formulario: FormularioOcorrencia = FormularioOcorrencia(),
    val editando: Boolean = false,
    val erroTitulo: String? = null,
    val erroEndereco: String? = null,
    val temAlteracoes: Boolean = false,
    val mostrarDialogoDescartar: Boolean = false,
    val concluido: Boolean = false,
)

fun Ocorrencia.paraFormulario() = FormularioOcorrencia(titulo, endereco, descricao, tipo, gravidade, resolvida)

class OcorrenciaFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: OcorrenciaRepository,
) : ViewModel() {

    private val id: Long = savedStateHandle[ARG_ID] ?: SEM_ID
    private var original: Ocorrencia? = null
    private var formularioInicial = FormularioOcorrencia()

    private val _uiState = MutableStateFlow(FormUiState(editando = id != SEM_ID))
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    init {
        if (id != SEM_ID) {
            viewModelScope.launch {
                repository.buscarPorId(id)?.let { ocorrencia ->
                    original = ocorrencia
                    formularioInicial = ocorrencia.paraFormulario()
                    _uiState.update { it.copy(formulario = formularioInicial) }
                }
            }
        }
    }

    fun onTituloChange(valor: String) = editar { copy(titulo = valor) }
    fun onEnderecoChange(valor: String) = editar { copy(endereco = valor) }
    fun onDescricaoChange(valor: String) = editar { copy(descricao = valor) }
    fun onTipoChange(valor: TipoOcorrencia) = editar { copy(tipo = valor) }
    fun onGravidadeChange(valor: Gravidade) = editar { copy(gravidade = valor) }
    fun onResolvidaChange(valor: Boolean) = editar { copy(resolvida = valor) }

    private fun editar(transformacao: FormularioOcorrencia.() -> FormularioOcorrencia) {
        _uiState.update { estado ->
            val novo = estado.formulario.transformacao()
            estado.copy(
                formulario = novo,
                temAlteracoes = novo != formularioInicial,
                erroTitulo = if (novo.titulo.isNotBlank()) null else estado.erroTitulo,
                erroEndereco = if (novo.endereco.isNotBlank()) null else estado.erroEndereco,
            )
        }
    }

    /** Voltar (botão do sistema ou da TopAppBar): pede confirmação se houver alterações. */
    fun solicitarSaida() {
        _uiState.update {
            if (it.temAlteracoes) it.copy(mostrarDialogoDescartar = true) else it.copy(concluido = true)
        }
    }

    fun confirmarDescarte() = _uiState.update { it.copy(mostrarDialogoDescartar = false, concluido = true) }

    fun cancelarDescarte() = _uiState.update { it.copy(mostrarDialogoDescartar = false) }

    fun salvar() {
        val form = _uiState.value.formulario
        val erroTitulo = if (form.titulo.isBlank()) "Informe um título" else null
        val erroEndereco = if (form.endereco.isBlank()) "Informe o local da ocorrência" else null
        if (erroTitulo != null || erroEndereco != null) {
            _uiState.update { it.copy(erroTitulo = erroTitulo, erroEndereco = erroEndereco) }
            return
        }

        viewModelScope.launch {
            val ocorrencia = Ocorrencia(
                id = original?.id ?: 0L,
                titulo = form.titulo.trim(),
                endereco = form.endereco.trim(),
                descricao = form.descricao.trim(),
                tipo = form.tipo,
                gravidade = form.gravidade,
                resolvida = form.resolvida,
                criadaEm = original?.criadaEm ?: System.currentTimeMillis(),
            )
            repository.salvar(ocorrencia)
            _uiState.update { it.copy(concluido = true) }
        }
    }

    companion object {
        const val ARG_ID = "id"
        const val SEM_ID = -1L
    }
}
