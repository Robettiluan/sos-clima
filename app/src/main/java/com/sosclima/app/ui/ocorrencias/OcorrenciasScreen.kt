package com.sosclima.app.ui.ocorrencias

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sosclima.app.data.local.Ocorrencia
import com.sosclima.app.di.AppViewModelProvider
import com.sosclima.app.domain.formatarDataHora
import com.sosclima.app.ui.components.DialogoConfirmacao
import com.sosclima.app.ui.components.EstadoCarregando
import com.sosclima.app.ui.components.EstadoVazio
import com.sosclima.app.ui.components.Etiqueta
import com.sosclima.app.ui.theme.cor

@Composable
fun OcorrenciasRoute(
    onNova: () -> Unit,
    onEditar: (Long) -> Unit,
    viewModel: OcorrenciasViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Efeito controlado: coleta mensagens únicas fora da recomposição.
    LaunchedEffect(Unit) {
        viewModel.mensagens.collect { snackbarHostState.showSnackbar(it) }
    }

    OcorrenciasScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNova = onNova,
        onEditar = onEditar,
        onFiltro = viewModel::selecionarFiltro,
        onAlternarResolvida = viewModel::alternarResolvida,
        onExcluir = viewModel::solicitarExclusao,
        onLimparResolvidas = viewModel::solicitarLimpeza,
    )

    when (val dialogo = uiState.dialogo) {
        is DialogoOcorrencias.ConfirmarExclusao -> DialogoConfirmacao(
            titulo = "Excluir ocorrência?",
            mensagem = "\"${dialogo.ocorrencia.titulo}\" será apagada deste aparelho. Essa ação não pode ser desfeita.",
            textoConfirmar = "Excluir",
            onConfirmar = viewModel::confirmarDialogo,
            onCancelar = viewModel::fecharDialogo,
            icone = Icons.Default.Delete,
        )
        is DialogoOcorrencias.ConfirmarLimpeza -> DialogoConfirmacao(
            titulo = "Limpar histórico?",
            mensagem = "${dialogo.quantidade} ocorrência(s) marcada(s) como resolvida(s) serão apagadas.",
            textoConfirmar = "Limpar",
            onConfirmar = viewModel::confirmarDialogo,
            onCancelar = viewModel::fecharDialogo,
        )
        DialogoOcorrencias.Nenhum -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcorrenciasScreen(
    uiState: OcorrenciasUiState,
    snackbarHostState: SnackbarHostState,
    onNova: () -> Unit,
    onEditar: (Long) -> Unit,
    onFiltro: (FiltroOcorrencia) -> Unit,
    onAlternarResolvida: (Ocorrencia) -> Unit,
    onExcluir: (Ocorrencia) -> Unit,
    onLimparResolvidas: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ocorrências") },
                actions = {
                    TextButton(onClick = onLimparResolvidas, enabled = uiState.totalResolvidas > 0) {
                        Text("Limpar resolvidas")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNova) {
                Icon(Icons.Default.Add, contentDescription = "Registrar ocorrência")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                FiltroOcorrencia.entries.forEach { filtro ->
                    FilterChip(
                        selected = uiState.filtro == filtro,
                        onClick = { onFiltro(filtro) },
                        label = { Text(filtro.rotulo) },
                        leadingIcon = if (uiState.filtro == filtro) {
                            { Icon(Icons.Default.Done, contentDescription = null) }
                        } else {
                            null
                        },
                    )
                }
            }

            when {
                uiState.carregando -> EstadoCarregando()
                uiState.itens.isEmpty() -> EstadoVazio(
                    titulo = "Nenhuma ocorrência",
                    mensagem = "Toque no + para registrar um alagamento, árvore caída ou outro problema no seu bairro.",
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.itens, key = { it.id }) { ocorrencia ->
                        CartaoOcorrencia(
                            ocorrencia = ocorrencia,
                            onClick = { onEditar(ocorrencia.id) },
                            onAlternarResolvida = { onAlternarResolvida(ocorrencia) },
                            onExcluir = { onExcluir(ocorrencia) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartaoOcorrencia(
    ocorrencia: Ocorrencia,
    onClick: () -> Unit,
    onAlternarResolvida: () -> Unit,
    onExcluir: () -> Unit,
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    ocorrencia.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (ocorrencia.resolvida) TextDecoration.LineThrough else null,
                )
                Text(ocorrencia.endereco, style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                    Etiqueta(ocorrencia.tipo.rotulo, MaterialTheme.colorScheme.primary)
                    Etiqueta("Gravidade ${ocorrencia.gravidade.rotulo}", ocorrencia.gravidade.cor())
                }
                Text(
                    ocorrencia.criadaEm.formatarDataHora(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            IconButton(onClick = onAlternarResolvida) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = if (ocorrencia.resolvida) "Reabrir" else "Marcar como resolvida",
                    tint = if (ocorrencia.resolvida) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    },
                )
            }
            IconButton(onClick = onExcluir) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir")
            }
        }
    }
}
