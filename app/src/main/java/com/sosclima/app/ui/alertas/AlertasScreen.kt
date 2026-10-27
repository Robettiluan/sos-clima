package com.sosclima.app.ui.alertas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sosclima.app.di.AppViewModelProvider
import com.sosclima.app.domain.PrevisaoDia
import com.sosclima.app.domain.descricaoTempo
import com.sosclima.app.domain.formatarCurto
import com.sosclima.app.domain.formatarGraus
import com.sosclima.app.domain.nivelAlerta
import com.sosclima.app.ui.components.EstadoCarregando
import com.sosclima.app.ui.components.EstadoErro
import com.sosclima.app.ui.components.EstadoVazio
import com.sosclima.app.ui.components.Etiqueta
import com.sosclima.app.ui.theme.cor

/** Tela "com ViewModel": busca o estado e repassa para a tela sem estado (state hoisting). */
@Composable
fun AlertasRoute(
    onAbrirDetalhe: (PrevisaoDia) -> Unit,
    viewModel: AlertasViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AlertasScreen(
        uiState = uiState,
        onTentarNovamente = viewModel::tentarNovamente,
        onAbrirDetalhe = onAbrirDetalhe,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertasScreen(
    uiState: AlertasUiState,
    onTentarNovamente: () -> Unit,
    onAbrirDetalhe: (PrevisaoDia) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val subtitulo = (uiState as? AlertasUiState.Success)?.cidade?.nome
                    Column {
                        Text("Alertas climáticos")
                        if (subtitulo != null) {
                            Text(subtitulo, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onTentarNovamente) {
                        Icon(Icons.Default.Refresh, contentDescription = "Atualizar previsão")
                    }
                },
            )
        },
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (uiState) {
            is AlertasUiState.Loading -> EstadoCarregando(modifier, "Buscando previsão...")
            is AlertasUiState.Error -> EstadoErro(uiState.mensagem, onTentarNovamente, modifier)
            is AlertasUiState.Success ->
                if (uiState.dias.isEmpty()) {
                    EstadoVazio("Sem dados", "A API não retornou previsão para esta cidade.", modifier)
                } else {
                    LazyColumn(
                        modifier = modifier,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(uiState.dias, key = { it.data.toString() }) { dia ->
                            CartaoPrevisao(dia = dia, onClick = { onAbrirDetalhe(dia) })
                        }
                    }
                }
        }
    }
}

@Composable
private fun CartaoPrevisao(dia: PrevisaoDia, onClick: () -> Unit) {
    val nivel = dia.nivelAlerta()
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    dia.data.formatarCurto(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Etiqueta(nivel.rotulo, nivel.cor())
            }
            Text(dia.codigoTempo.descricaoTempo(), style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(top = 8.dp)) {
                Text("${dia.tempMin.formatarGraus()} / ${dia.tempMax.formatarGraus()}")
                Text("Chuva ${"%.1f".format(dia.chuvaMm)} mm")
                Text("Vento ${"%.0f".format(dia.ventoKmh)} km/h")
            }
        }
    }
}
